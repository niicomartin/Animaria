package com.PetShop.Controladores;

import com.PetShop.Servicios.EmailServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmailControlador {

    @Autowired
    private EmailServicio emailServicio;

    @GetMapping("/admin/email")
    public String vistaEmail(ModelMap modelo) {
        modelo.put("ultimoError", emailServicio.getUltimoError());
        modelo.put("ultimoLink", emailServicio.getUltimoLinkGenerado());
        return "admin-email.html";
    }

    @PostMapping("/admin/email/probar")
    public String probarEmail(ModelMap modelo, @RequestParam String email) {
        try {
            emailServicio.enviarCorreoPrueba(email);
            modelo.put("exito", "Correo de prueba enviado. Revisá también spam/no deseado.");
        } catch (Exception e) {
            modelo.put("error", "No se pudo enviar el correo: " + e.getMessage());
        }
        modelo.put("email", email);
        modelo.put("ultimoError", emailServicio.getUltimoError());
        modelo.put("ultimoLink", emailServicio.getUltimoLinkGenerado());
        return "admin-email.html";
    }
}
