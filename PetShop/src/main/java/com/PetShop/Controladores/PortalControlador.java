package com.PetShop.Controladores;

import com.PetShop.Entidades.Alimento;
import com.PetShop.Servicios.AlimentoServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PortalControlador {

    @Autowired
    private AlimentoServicio alimentoServicio;

    @GetMapping("/login")
    public String login(ModelMap modelo,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout) {
        if (error != null) {
            modelo.put("error", "Email o contraseña incorrectos. Si recién te registraste, validá tu cuenta desde el correo.");
        }
        if (logout != null) {
            modelo.put("logout", "Has cerrado sesión correctamente");
        }
        return "login.html";
    }

    @GetMapping("/registro")
    public String registro() {
        return "registro.html";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/accesorio/crear")
    public String crearAccesorio() {
        return "accesorio-crear.html";
    }

    @GetMapping("/alimento")
    public String alimento(ModelMap modelo,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String animal) {
        List<Alimento> alimentos;

        if ((tipo == null || tipo.trim().isEmpty()) && (animal == null || animal.trim().isEmpty())) {
            alimentos = alimentoServicio.listar();
        } else if (tipo == null || tipo.trim().isEmpty()) {
            alimentos = alimentoServicio.filtrarPorAnimal(animal);
        } else {
            alimentos = alimentoServicio.filtrar(tipo, animal);
        }

        modelo.put("alimentos", alimentos);
        modelo.put("animalSeleccionado", animal);
        modelo.put("tipoSeleccionado", tipo);
        return "alimento.html";
    }

    @GetMapping("/CarritoCompra")
    public String carritoViejo() {
        return "redirect:/Carrito/ver";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "contacto.html";
    }

    @GetMapping("/quienes_somos")
    public String quienesSomos() {
        return "quienes_somos.html";
    }

    @GetMapping("/login/usuario")
    public String usuarioViejo() {
        return "redirect:/mi-cuenta";
    }

    @GetMapping("/login/contraseñaOlvidada")
    public String contraseñaOlvidada() {
        return "contraseñaOlvidada.html";
    }
}
