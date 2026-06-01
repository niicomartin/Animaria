package com.PetShop.Controladores;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Usuario;
import com.PetShop.Servicios.AccesorioServicio;
import com.PetShop.Servicios.AlimentoServicio;
import com.PetShop.Servicios.CompraServicio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/Carrito")
@PreAuthorize("hasAnyRole('GENERAL', 'ADMIN')")
public class CompraControlador {

    @Autowired
    private CompraServicio compraServicio;

    @Autowired
    private AlimentoServicio alimentoServicio;

    @Autowired
    private AccesorioServicio accesorioServicio;

    @GetMapping({"", "/ver"})
    public String verCarrito(ModelMap modelo, HttpSession session) {
        cargarDatosCarrito(modelo, session);
        return "CarritoCompra.html";
    }

    @GetMapping("/crear/{id}")
    public String agregarViejo(@PathVariable String id, HttpSession session) throws Exception {
        return agregarAlimento(id, session);
    }

    @GetMapping("/agregar-alimento/{id}")
    public String agregarAlimento(@PathVariable String id, HttpSession session) throws Exception {
        Alimento alimento = alimentoServicio.buscarPorId(id);
        List<Alimento> alimentos = obtenerAlimentos(session);
        alimentos.add(alimento);
        session.setAttribute("carritoAlimentos", alimentos);
        return "redirect:/Carrito/ver";
    }

    @GetMapping("/agregar-accesorio/{id}")
    public String agregarAccesorio(@PathVariable String id, HttpSession session) throws Exception {
        Accesorio accesorio = accesorioServicio.buscarPorId(id);
        List<Accesorio> accesorios = obtenerAccesorios(session);
        accesorios.add(accesorio);
        session.setAttribute("carritoAccesorios", accesorios);
        return "redirect:/Carrito/ver";
    }

    @GetMapping("/eliminar-alimento/{indice}")
    public String eliminarAlimento(@PathVariable Integer indice, HttpSession session) {
        List<Alimento> alimentos = obtenerAlimentos(session);
        if (indice >= 0 && indice < alimentos.size()) alimentos.remove(indice.intValue());
        session.setAttribute("carritoAlimentos", alimentos);
        return "redirect:/Carrito/ver";
    }

    @GetMapping("/eliminar-accesorio/{indice}")
    public String eliminarAccesorio(@PathVariable Integer indice, HttpSession session) {
        List<Accesorio> accesorios = obtenerAccesorios(session);
        if (indice >= 0 && indice < accesorios.size()) accesorios.remove(indice.intValue());
        session.setAttribute("carritoAccesorios", accesorios);
        return "redirect:/Carrito/ver";
    }

    @GetMapping("/vaciar")
    public String vaciar(HttpSession session) {
        session.removeAttribute("carritoAlimentos");
        session.removeAttribute("carritoAccesorios");
        return "redirect:/Carrito/ver";
    }

    @PostMapping("/confirmar")
    public String confirmar(ModelMap modelo, HttpSession session,
            @RequestParam String direccionEnvio,
            @RequestParam String localidadEnvio,
            @RequestParam String provinciaEnvio,
            @RequestParam(required = false) String codigoPostalEnvio,
            @RequestParam String telefonoContacto,
            @RequestParam(required = false) String observaciones) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            compraServicio.crearCompra(obtenerAlimentos(session), obtenerAccesorios(session), usuario,
                    direccionEnvio, localidadEnvio, provinciaEnvio, codigoPostalEnvio, telefonoContacto, observaciones);
            session.removeAttribute("carritoAlimentos");
            session.removeAttribute("carritoAccesorios");
            return "redirect:/mi-cuenta?compra=ok";
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            cargarDatosCarrito(modelo, session);
            return "CarritoCompra.html";
        }
    }

    private void cargarDatosCarrito(ModelMap modelo, HttpSession session) {
        List<Alimento> alimentos = obtenerAlimentos(session);
        List<Accesorio> accesorios = obtenerAccesorios(session);
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        modelo.put("alimentos", alimentos);
        modelo.put("accesorios", accesorios);
        modelo.put("total", compraServicio.calcularTotal(alimentos, accesorios));
        modelo.put("usuario", usuario);
    }

    private List<Alimento> obtenerAlimentos(HttpSession session) {
        List<Alimento> alimentos = (List<Alimento>) session.getAttribute("carritoAlimentos");
        return alimentos == null ? new ArrayList<Alimento>() : alimentos;
    }

    private List<Accesorio> obtenerAccesorios(HttpSession session) {
        List<Accesorio> accesorios = (List<Accesorio>) session.getAttribute("carritoAccesorios");
        return accesorios == null ? new ArrayList<Accesorio>() : accesorios;
    }
}
