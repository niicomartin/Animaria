package com.PetShop.Controladores;

import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.Usuario;
import com.PetShop.Servicios.CompraServicio;
import com.PetShop.Servicios.UsuarioServicio;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAnyRole('GENERAL', 'ADMIN')")
public class MiCuentaControlador {

    @Autowired
    private CompraServicio compraServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/mi-cuenta")
    public String miCuenta(ModelMap modelo, HttpSession session,
            @RequestParam(required = false) String compra) {
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        List<Compra> compras = compraServicio.buscarPorUsuario(usuario);

        modelo.put("usuario", usuario);
        modelo.put("compras", compras);
        if (compra != null) modelo.put("exito", "Compra confirmada correctamente");

        return "mi-cuenta.html";
    }

    @PostMapping("/mi-cuenta/editar")
    public String editar(ModelMap modelo, HttpSession session,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam Long celular,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
            @RequestParam String direccion,
            @RequestParam String localidad,
            @RequestParam String provincia,
            @RequestParam(required = false) String codigoPostal) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            Usuario actualizado = usuarioServicio.editar(usuario.getId(), nombre, apellido, celular, fechaNacimiento,
                    direccion, localidad, provincia, codigoPostal);
            session.setAttribute("usuariosession", actualizado);
            return "redirect:/mi-cuenta";
        } catch (Exception e) {
            Usuario usuario = (Usuario) session.getAttribute("usuariosession");
            List<Compra> compras = compraServicio.buscarPorUsuario(usuario);
            modelo.put("usuario", usuario);
            modelo.put("compras", compras);
            modelo.put("error", e.getMessage());
            return "mi-cuenta.html";
        }
    }
}
