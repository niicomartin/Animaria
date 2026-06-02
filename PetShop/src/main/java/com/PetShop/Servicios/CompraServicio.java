package com.PetShop.Servicios;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.EstadoCompra;
import com.PetShop.Entidades.Usuario;
import com.PetShop.Repositorios.AccesorioRepositorio;
import com.PetShop.Repositorios.AlimentoRepositorio;
import com.PetShop.Repositorios.CompraRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraServicio {

    @Autowired
    private CompraRepositorio compraRepositorio;

    @Autowired
    private AlimentoRepositorio alimentoRepositorio;

    @Autowired
    private AccesorioRepositorio accesorioRepositorio;

    @Transactional(rollbackFor = {Exception.class})
    public Compra crearCompra(List<Alimento> alimentos, List<Accesorio> accesorios, Usuario usuario,
            String direccionEnvio, String localidadEnvio, String provinciaEnvio, String codigoPostalEnvio,
            String telefonoContacto, String observaciones) throws Exception {

        validarCarrito(alimentos, accesorios, usuario);
        validarEnvio(direccionEnvio, localidadEnvio, provinciaEnvio, telefonoContacto);

        // Se vuelve a buscar cada producto desde la base, valida stock real y descuenta.
        // Si algo falla, toda la compra se cancela por la transacción y no descuenta nada.
        List<Alimento> alimentosConfirmados = descontarStockAlimentos(alimentos);
        List<Accesorio> accesoriosConfirmados = descontarStockAccesorios(accesorios);

        double total = calcularTotal(alimentosConfirmados, accesoriosConfirmados);

        Compra compra = new Compra();
        compra.setAlimentos(alimentosConfirmados);
        compra.setAccesorios(accesoriosConfirmados);
        compra.setUsuario(usuario);
        compra.setValorCompra(total);
        compra.setTotal(total);
        compra.setAlta(true);
        compra.setFechaAlta(new Date());
        compra.setEstado(EstadoCompra.PENDIENTE);
        compra.setMetodoPago("PAGO_EN_DOMICILIO");
        compra.setDireccionEnvio(direccionEnvio);
        compra.setLocalidadEnvio(localalidadSinNulo(localidadEnvio));
        compra.setProvinciaEnvio(provinciaEnvio);
        compra.setCodigoPostalEnvio(codigoPostalEnvio);
        compra.setTelefonoContacto(telefonoContacto);
        compra.setObservaciones(observaciones);
        compra.setNotaAdmin("Pedido nuevo. Coordinar entrega y cobro en domicilio.");

        return compraRepositorio.save(compra);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Compra crearCompra(List<Alimento> alimentos, List<Accesorio> accesorios, Usuario usuario) throws Exception {
        String direccion = usuario != null ? usuario.getDireccion() : null;
        String localidad = usuario != null ? usuario.getLocalidad() : null;
        String provincia = usuario != null ? usuario.getProvincia() : null;
        String cp = usuario != null ? usuario.getCodigoPostal() : null;
        String telefono = usuario != null && usuario.getCelular() != null ? usuario.getCelular().toString() : null;
        return crearCompra(alimentos, accesorios, usuario, direccion, localidad, provincia, cp, telefono, "");
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarActivas() {
        List<Compra> compras = compraRepositorio.buscarActivas();
        prepararComprasParaVista(compras);
        return compras;
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorEstado(EstadoCompra estado) {
        List<Compra> compras;
        if (estado == null) {
            compras = compraRepositorio.buscarActivas();
        } else {
            compras = compraRepositorio.findByEstadoOrderByFechaAltaDesc(estado);
        }
        prepararComprasParaVista(compras);
        return compras;
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorUsuario(Usuario usuario) {
        List<Compra> compras = compraRepositorio.findByUsuarioOrderByFechaAltaDesc(usuario);
        prepararComprasParaVista(compras);
        return compras;
    }

    @Transactional(readOnly = true)
    public List<Compra> MostrarTodas() {
        return compraRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public Compra buscarPorId(String id) throws Exception {
        Optional<Compra> respuesta = compraRepositorio.findById(id);
        if (respuesta.isPresent()) return respuesta.get();
        throw new Exception("No existe esta compra con ese id");
    }

    @Transactional(rollbackFor = {Exception.class})
    public Compra actualizarEstado(String id, EstadoCompra estado, String notaAdmin) throws Exception {
        if (estado == null) {
            throw new Exception("El estado es obligatorio");
        }

        // Si el administrador elige CANCELADO desde el selector, se usa el mismo flujo
        // que el botón de cancelar: repone stock, oculta el pedido del panel admin
        // y deja una nota visible para el cliente en Mi cuenta.
        if (estado == EstadoCompra.CANCELADO) {
            return cancelarPedido(id, notaAdmin);
        }

        Compra compra = buscarPorId(id);
        compra.setEstado(estado);
        compra.setNotaAdmin(limpiarNota(notaAdmin, "Pedido actualizado por Animaria."));
        return compraRepositorio.save(compra);
    }

    @Transactional(rollbackFor = {Exception.class})
    public Compra cancelarPedido(String id, String notaAdmin) throws Exception {
        Compra compra = buscarPorId(id);

        // Evita duplicar stock si alguien toca cancelar dos veces o si el pedido ya estaba cancelado.
        boolean pedidoActivo = compra.getAlta() != null && compra.getAlta();
        boolean yaCancelado = compra.getEstado() == EstadoCompra.CANCELADO;

        if (pedidoActivo && !yaCancelado) {
            reponerStockAlimentos(compra.getAlimentos());
            reponerStockAccesorios(compra.getAccesorios());
        }

        compra.setAlta(false);
        compra.setEstado(EstadoCompra.CANCELADO);
        compra.setNotaAdmin(limpiarNota(notaAdmin,
                "Tu pedido fue cancelado por Animaria. El stock fue devuelto correctamente."));

        return compraRepositorio.save(compra);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void eliminar(String id) throws Exception {
        cancelarPedido(id, "Tu pedido fue cancelado por Animaria. El stock fue devuelto correctamente.");
    }

    private List<Alimento> descontarStockAlimentos(List<Alimento> alimentos) throws Exception {
        List<Alimento> alimentosConfirmados = new ArrayList<>();

        if (alimentos == null || alimentos.isEmpty()) {
            return alimentosConfirmados;
        }

        Map<String, Integer> cantidadesPorProducto = new LinkedHashMap<>();

        for (Alimento item : alimentos) {
            if (item == null || item.getId() == null) {
                continue;
            }
            Integer cantidadActual = cantidadesPorProducto.get(item.getId());
            cantidadesPorProducto.put(item.getId(), cantidadActual == null ? 1 : cantidadActual + 1);
        }

        for (Map.Entry<String, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Alimento alimento = alimentoRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new Exception("Uno de los alimentos del carrito ya no existe"));

            int cantidadComprada = entrada.getValue();
            int stockActual = alimento.getStock() == null ? 0 : alimento.getStock();

            if (!alimento.isActivo()) {
                throw new Exception("El producto " + alimento.getMarca() + " ya no está disponible");
            }

            if (stockActual < cantidadComprada) {
                throw new Exception("Stock insuficiente para " + alimento.getMarca()
                        + ". Disponible: " + stockActual + ". Solicitado: " + cantidadComprada);
            }

            alimento.setStock(stockActual - cantidadComprada);
            alimentoRepositorio.save(alimento);

            for (int i = 0; i < cantidadComprada; i++) {
                alimentosConfirmados.add(alimento);
            }
        }

        return alimentosConfirmados;
    }

    private List<Accesorio> descontarStockAccesorios(List<Accesorio> accesorios) throws Exception {
        List<Accesorio> accesoriosConfirmados = new ArrayList<>();

        if (accesorios == null || accesorios.isEmpty()) {
            return accesoriosConfirmados;
        }

        Map<String, Integer> cantidadesPorProducto = new LinkedHashMap<>();

        for (Accesorio item : accesorios) {
            if (item == null || item.getId() == null) {
                continue;
            }
            Integer cantidadActual = cantidadesPorProducto.get(item.getId());
            cantidadesPorProducto.put(item.getId(), cantidadActual == null ? 1 : cantidadActual + 1);
        }

        for (Map.Entry<String, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Accesorio accesorio = accesorioRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new Exception("Uno de los accesorios del carrito ya no existe"));

            int cantidadComprada = entrada.getValue();
            int stockActual = accesorio.getStock() == null ? 0 : accesorio.getStock();

            if (!accesorio.isActivo()) {
                throw new Exception("El accesorio " + accesorio.getNombre() + " ya no está disponible");
            }

            if (stockActual < cantidadComprada) {
                throw new Exception("Stock insuficiente para " + accesorio.getNombre()
                        + ". Disponible: " + stockActual + ". Solicitado: " + cantidadComprada);
            }

            accesorio.setStock(stockActual - cantidadComprada);
            accesorioRepositorio.save(accesorio);

            for (int i = 0; i < cantidadComprada; i++) {
                accesoriosConfirmados.add(accesorio);
            }
        }

        return accesoriosConfirmados;
    }

    private void reponerStockAlimentos(List<Alimento> alimentos) throws Exception {
        if (alimentos == null || alimentos.isEmpty()) {
            return;
        }

        Map<String, Integer> cantidadesPorProducto = new LinkedHashMap<>();

        for (Alimento item : alimentos) {
            if (item == null || item.getId() == null) {
                continue;
            }
            Integer cantidadActual = cantidadesPorProducto.get(item.getId());
            cantidadesPorProducto.put(item.getId(), cantidadActual == null ? 1 : cantidadActual + 1);
        }

        for (Map.Entry<String, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Alimento alimento = alimentoRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new Exception("No se pudo reponer stock de un alimento"));
            int stockActual = alimento.getStock() == null ? 0 : alimento.getStock();
            alimento.setStock(stockActual + entrada.getValue());
            alimentoRepositorio.save(alimento);
        }
    }

    private void reponerStockAccesorios(List<Accesorio> accesorios) throws Exception {
        if (accesorios == null || accesorios.isEmpty()) {
            return;
        }

        Map<String, Integer> cantidadesPorProducto = new LinkedHashMap<>();

        for (Accesorio item : accesorios) {
            if (item == null || item.getId() == null) {
                continue;
            }
            Integer cantidadActual = cantidadesPorProducto.get(item.getId());
            cantidadesPorProducto.put(item.getId(), cantidadActual == null ? 1 : cantidadActual + 1);
        }

        for (Map.Entry<String, Integer> entrada : cantidadesPorProducto.entrySet()) {
            Accesorio accesorio = accesorioRepositorio.findById(entrada.getKey())
                    .orElseThrow(() -> new Exception("No se pudo reponer stock de un accesorio"));
            int stockActual = accesorio.getStock() == null ? 0 : accesorio.getStock();
            accesorio.setStock(stockActual + entrada.getValue());
            accesorioRepositorio.save(accesorio);
        }
    }

    private void prepararComprasParaVista(List<Compra> compras) {
        if (compras == null) {
            return;
        }

        for (Compra compra : compras) {
            if (compra.getEstado() == null) {
                compra.setEstado(EstadoCompra.PENDIENTE);
            }
            if (compra.getMetodoPago() == null || compra.getMetodoPago().trim().isEmpty()) {
                compra.setMetodoPago("PAGO_EN_DOMICILIO");
            }
            if (compra.getNotaAdmin() == null || compra.getNotaAdmin().trim().isEmpty()) {
                compra.setNotaAdmin("Pedido recibido. Revisar y coordinar entrega.");
            }

            if (compra.getAlimentos() != null) {
                compra.getAlimentos().size();
            }
            if (compra.getAccesorios() != null) {
                compra.getAccesorios().size();
            }
        }
    }

    public void validarCarrito(List<Alimento> alimentos, List<Accesorio> accesorios, Usuario usuario) throws Exception {
        boolean sinAlimentos = alimentos == null || alimentos.isEmpty();
        boolean sinAccesorios = accesorios == null || accesorios.isEmpty();
        if (sinAlimentos && sinAccesorios) throw new Exception("No hay productos en el carrito");
        if (usuario == null) throw new Exception("Tenés que iniciar sesión para comprar");
    }

    private void validarEnvio(String direccion, String localidad, String provincia, String telefono) throws Exception {
        if (direccion == null || direccion.trim().isEmpty()) throw new Exception("Ingresá la dirección de envío");
        if (localidad == null || localidad.trim().isEmpty()) throw new Exception("Ingresá la localidad de envío");
        if (provincia == null || provincia.trim().isEmpty()) throw new Exception("Ingresá la provincia de envío");
        if (telefono == null || telefono.trim().isEmpty()) throw new Exception("Ingresá un teléfono de contacto");
    }

    public double calcularTotal(List<Alimento> alimentos, List<Accesorio> accesorios) {
        double total = 0;
        if (alimentos != null) {
            for (Alimento alimento : alimentos) total += alimento.getPrecio();
        }
        if (accesorios != null) {
            for (Accesorio accesorio : accesorios) {
                if (accesorio.getPrecio() != null) total += accesorio.getPrecio();
            }
        }
        return total;
    }

    private String limpiarNota(String notaAdmin, String notaPorDefecto) {
        if (notaAdmin == null || notaAdmin.trim().isEmpty()) {
            return notaPorDefecto;
        }
        return notaAdmin.trim();
    }

    private String localalidadSinNulo(String localidad) {
        return localidad == null ? "" : localidad;
    }
}
