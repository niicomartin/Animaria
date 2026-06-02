package com.PetShop.Controladores;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Servicios.AccesorioServicio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

@RequestMapping("/accesorio")
public class AccesorioControlador {

    @Autowired
    public AccesorioServicio accesorioServicio;

    @GetMapping
    public String listar(ModelMap modelo) {
        List<Accesorio> accesorios = accesorioServicio.listar();
        modelo.put("accesorios", accesorios);
        System.out.println(accesorios);
        return "accesorio.html";

    }
@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ingresarAccesorio")
    public String crear(ModelMap modelo, @RequestParam String marca, @RequestParam Integer stock, @RequestParam String nombre, @RequestParam Long precio, @RequestParam MultipartFile archivo) {

        try {
            accesorioServicio.crear(marca, stock, nombre, precio, archivo);
            modelo.put("exito", "El accesorio fue cargado correctamente");

        } catch (Exception e) {
            modelo.put("error", e.getMessage());

        }
        return "redirect:/accesorio";
    }
@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/editar")
    public String editar(ModelMap modelo, @RequestParam String id, @RequestParam String nombre, @RequestParam String marca, @RequestParam Integer stock, @RequestParam Long precio, @RequestParam MultipartFile archivo) {
        try {
            accesorioServicio.editar(id, marca, stock, nombre, precio, archivo);
            modelo.put("exito", "La modificacion se realizo correctamente");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "redirect:/accesorio";

    }
@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable String id, ModelMap modelo) throws Exception {
        Accesorio accesorio = accesorioServicio.buscarPorId(id);
        modelo.put("accesorio", accesorio);

        return "accesorio-editar.html";
    }
@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable String id) throws Exception {
        accesorioServicio.darBaja(id);
        return "redirect:/accesorio";
    }
}
