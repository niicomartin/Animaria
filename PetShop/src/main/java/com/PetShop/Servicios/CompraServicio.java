package com.PetShop.Servicios;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.EstadoCompra;
import com.PetShop.Entidades.Usuario;
import com.PetShop.Repositorios.CompraRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraServicio {

    @Autowired
    private CompraRepositorio compraRepositorio;

    @Transactional(rollbackFor = {Exception.class})
    public Compra crearCompra(List<Alimento> alimentos, List<Accesorio> accesorios, Usuario usuario,
            String direccionEnvio, String localidadEnvio, String provinciaEnvio, String codigoPostalEnvio,
            String telefonoContacto, String observaciones) throws Exception {

        validarCarrito(alimentos, accesorios, usuario);
        validarEnvio(direccionEnvio, localidadEnvio, provinciaEnvio, telefonoContacto);

        double total = calcularTotal(alimentos, accesorios);

        Compra compra = new Compra();
        compra.setAlimentos(alimentos);
        compra.setAccesorios(accesorios);
        compra.setUsuario(usuario);
        compra.setValorCompra(total);
        compra.setTotal(total);
        compra.setAlta(true);
        compra.setFechaAlta(new Date());
        compra.setEstado(EstadoCompra.PENDIENTE);
        compra.setMetodoPago("PAGO_EN_DOMICILIO");
        compra.setDireccionEnvio(direccionEnvio);
        compra.setLocalidadEnvio(localidadEnvio);
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
        return compraRepositorio.buscarActivas();
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorEstado(EstadoCompra estado) {
        if (estado == null) return buscarActivas();
        return compraRepositorio.findByEstadoOrderByFechaAltaDesc(estado);
    }

    @Transactional(readOnly = true)
    public List<Compra> buscarPorUsuario(Usuario usuario) {
        return compraRepositorio.findByUsuarioOrderByFechaAltaDesc(usuario);
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
        Compra compra = buscarPorId(id);
        if (estado == null) throw new Exception("El estado es obligatorio");
        compra.setEstado(estado);
        compra.setNotaAdmin(notaAdmin);
        return compraRepositorio.save(compra);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void eliminar(String id) throws Exception {
        Compra compra = buscarPorId(id);
        compra.setAlta(false);
        compra.setEstado(EstadoCompra.CANCELADO);
        compraRepositorio.save(compra);
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
}
