package com.PetShop.Controladores;

import com.PetShop.Entidades.Usuario;
import com.PetShop.Servicios.UsuarioServicio;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @PostMapping("/registro")
    public String registro(ModelMap modelo,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam Long celular,
            @RequestParam String email,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaNacimiento,
            @RequestParam String direccion,
            @RequestParam String localidad,
            @RequestParam String provincia,
            @RequestParam(required = false) String codigoPostal,
            @RequestParam String password,
            @RequestParam String password2) {
        try {
            Usuario usuario = usuarioServicio.crear(nombre, apellido, celular, email, fechaNacimiento,
                    direccion, localidad, provincia, codigoPostal, password, password2);

            modelo.put("exito", "Registro exitoso. Ya podés iniciar sesión y realizar tus compras.");
            return "login.html";
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("celular", celular);
            modelo.put("email", email);
            modelo.put("fechaNacimiento", fechaNacimiento);
            modelo.put("direccion", direccion);
            modelo.put("localidad", localidad);
            modelo.put("provincia", provincia);
            modelo.put("codigoPostal", codigoPostal);
            return "registro.html";
        }
    }

    @GetMapping("/usuario/validar/{token}")
    public String validarCuenta(@PathVariable String token, ModelMap modelo) {
        try {
            usuarioServicio.validarCuenta(token);
            modelo.put("exito", "Cuenta validada correctamente. Ya podés iniciar sesión.");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "login.html";
    }

    @GetMapping("/login/olvido")
    public String olvidoClave() {
        return "olvido-clave.html";
    }

    @PostMapping("/login/olvido")
    public String solicitarRecuperacion(ModelMap modelo, @RequestParam String email) {
        try {
            Usuario usuario = usuarioServicio.solicitarRecuperacionPassword(email);
            modelo.put("exito", "Te enviamos un correo para restablecer tu contraseña. Revisá spam/no deseado.");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            modelo.put("email", email);
        }
        return "olvido-clave.html";
    }

    @GetMapping("/login/restablecer/{token}")
    public String vistaRestablecer(@PathVariable String token, ModelMap modelo) {
        try {
            usuarioServicio.buscarPorTokenRecuperacion(token);
            modelo.put("token", token);
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "restablecer-clave.html";
    }

    @PostMapping("/login/restablecer")
    public String restablecerPassword(ModelMap modelo,
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String password2) {
        try {
            usuarioServicio.cambiarPasswordConToken(token, password, password2);
            modelo.put("exito", "Contraseña actualizada correctamente. Ya podés iniciar sesión.");
            return "login.html";
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            modelo.put("token", token);
            return "restablecer-clave.html";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lista")
    public String lista(ModelMap modelo) {
        List<Usuario> usuarios = usuarioServicio.getAll();
        modelo.put("usuarios", usuarios);
        return "lista.html";
    }

    @PostMapping("/login/contraseñaOlvidada")
    public String contraseñaOlvidada(ModelMap modelo,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String nombre,
            @RequestParam Long celular) {
        try {
            usuarioServicio.modificarPassword(email, password, nombre, celular);
            modelo.put("exito", "Contraseña modificada exitosamente");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "contraseñaOlvidada.html";
    }
}
