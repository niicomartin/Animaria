package com.PetShop.Controladores;

import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Animal;
import com.PetShop.Entidades.TipoAlimento;
import com.PetShop.Servicios.AlimentoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AlimentoControlador {

    @Autowired
    private AlimentoServicio alimentoServicio;

    @GetMapping("/alimento/crear")
    public String crear(ModelMap modelo) {
        modelo.put("tipoAlimentos", TipoAlimento.values());
        return "alimento-crear.html";
    }

    @PostMapping("/alimento/crear")
    public String crear(
            ModelMap modelo,
            @RequestParam Integer cantidad,
            @RequestParam Animal animal,
            @RequestParam TipoAlimento tipoAlimento,
            @RequestParam String marca,
            @RequestParam Integer stock,
            @RequestParam double precio,
            @RequestParam(required = false) MultipartFile archivo) {

        try {
            alimentoServicio.crear(cantidad, animal, tipoAlimento, marca, stock, precio, archivo);
            return "redirect:/alimento";
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            modelo.put("tipoAlimentos", TipoAlimento.values());
            return "alimento-crear.html";
        }
    }

    @GetMapping("/alimento/editar/{id}")
    public String editar(ModelMap modelo, @PathVariable String id) {
        try {
            Alimento alimento = alimentoServicio.buscarPorId(id);
            modelo.put("alimento", alimento);
            modelo.put("tipoAlimentos", TipoAlimento.values());
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "alimento-editar.html";
    }

    @PostMapping("/alimento/editar/{id}")
    public String editar(
            ModelMap modelo,
            @PathVariable String id,
            @RequestParam Integer cantidad,
            @RequestParam Animal animal,
            @RequestParam TipoAlimento tipoAlimento,
            @RequestParam String marca,
            @RequestParam Integer stock,
            @RequestParam double precio,
            @RequestParam(required = false) boolean activo,
            @RequestParam(required = false) MultipartFile archivo) {

        try {
            alimentoServicio.editar(id, cantidad, animal, tipoAlimento, marca, stock, precio, activo, archivo);
            return "redirect:/alimento";
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
            modelo.put("tipoAlimentos", TipoAlimento.values());
            return "alimento-editar.html";
        }
    }

    @GetMapping("/alimento/eliminar/{id}")
    public String eliminar(@PathVariable String id) {
        try {
            alimentoServicio.darBaja(id);
        } catch (Exception e) {
            System.out.println("No se pudo eliminar alimento: " + e.getMessage());
        }
        return "redirect:/alimento";
    }

    @PostMapping("/alimento/eliminar/{id}")
    public String eliminarPost(@PathVariable String id) {
        return eliminar(id);
    }
}
