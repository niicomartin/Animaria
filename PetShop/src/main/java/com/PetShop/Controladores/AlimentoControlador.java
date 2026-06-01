
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
    public AlimentoServicio alimentoServicio;

    @GetMapping("/alimento/crear")
    public String crear(ModelMap modelo){
        modelo.put("animales",Animal.values());
        modelo.put("tipoAlimentos", TipoAlimento.values());
        return "alimento-crear.html";
    }
    
    @PostMapping("/alimento/crear")
    public String crear(ModelMap modelo, @RequestParam Integer cantidad,@RequestParam Animal animal, @RequestParam TipoAlimento tipoAlimento,@RequestParam String marca, @RequestParam Integer stock, @RequestParam double precio, @RequestParam(required = false) MultipartFile archivo) {
        try {
            
            alimentoServicio.crear(cantidad, animal, tipoAlimento, marca, stock, precio, archivo);
            modelo.put("exito", "El alimento se cargo correctamente");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());

        }
        return "alimento-crear.html";
    }

    @GetMapping("/alimento/editar/{id}")
    public String editar(ModelMap modelo, @PathVariable String id) {
        try {
           
            Alimento alimento = alimentoServicio.buscarPorId(id);
            modelo.put("alimento", alimento);
            modelo.put("animales",Animal.values());
        modelo.put("tipoAlimentos", TipoAlimento.values());
        
        } catch (Exception e) {
            modelo.put("error", e.getMessage());
        }
        return "alimento-editar.html";
    }
    
    @PostMapping("/alimento/editar/{id}")
    public String editar(ModelMap modelo, @PathVariable String id, @RequestParam Integer cantidad, @RequestParam(required = false) Animal animal, @RequestParam(required = false) TipoAlimento tipoAlimento, @RequestParam(required = false) String marca, @RequestParam Integer stock, @RequestParam double precio, @RequestParam(required=false) boolean activo, @RequestParam(required = false) MultipartFile archivo) {
        try {
            
            
            
     alimentoServicio.editar(id, cantidad, animal, tipoAlimento, marca, stock, precio, activo, archivo);
            modelo.put("exito", "El alimento se cargo correctamente");
        } catch (Exception e) {
            modelo.put("error", e.getMessage());

       }
      return "redirect:/alimento";

   }
}

//@Controller
//public class AlimentoControlador {
//
//    @Autowired
//    public AlimentoServicio alimentoServicio;
//
//    @GetMapping("/alimento/crear")
//    public String crear(){
//        return "alimento-crear.html";
//    }
//    @PostMapping("/alimento/crear")
//    public String crear(ModelMap modelo, @RequestParam Integer cantidad,/*@RequestParam Animal animal, @RequestParam TipoAlimento tipoAlimento,*/ @RequestParam String marca, @RequestParam Integer stock, @RequestParam double precio, @RequestParam MultipartFile archivo) {
//        try {
//            alimentoServicio.crear(cantidad, /*animal, tipoAlimento,*/ marca, stock, precio, archivo);
//            modelo.put("exito", "El alimento se cargo correctamente");
//        } catch (Exception e) {
//            modelo.put("error", e.getMessage());
//
//        }
//        return "alimento-crear.html";
//    }
//
//    @GetMapping("")
//    public String editar(ModelMap modelo, @PathVariable String id) {
//        try {
//            Alimento alimento = alimentoServicio.buscarPorId(id);
//            modelo.put("alimento", alimento);
//        } catch (Exception e) {
//            modelo.put("error", e.getMessage());
//        }
//        return ".html";
//    }

//
//    @PostMapping("1")
//    public String editar(ModelMap modelo, @RequestParam String id, @RequestParam Integer cantidad, @RequestParam Animal animal, @RequestParam TipoAlimento tipoAlimento, @RequestParam String marca, @RequestParam Integer stock, @RequestParam double precio, @RequestParam boolean activo, @RequestParam MultipartFile archivo) {
//        try {
//            alimentoServicio.editar(id, cantidad, animal, tipoAlimento, marca, stock, precio, activo, archivo);
//            modelo.put("exito", "El alimento se cargo correctamente");
//        } catch (Exception e) {
//            modelo.put("error", e.getMessage());
//
//        }
//        return ".html";
//
//    }
//
//    @GetMapping("2")
//    public String listar(ModelMap modelo) {
//
//        List<Alimento> alimentos = alimentoServicio.listar();
//        modelo.put("alimentos",alimentos);
//        return ".html";
//    }
//    
//    @GetMapping("3")
//    public String filtrarPorAnimal(ModelMap modelo, @PathVariable Animal animal){
//        
//        List<Alimento> alimentos = alimentoServicio.filtrarPorAnmial(animal);
//        modelo.put("alimentos",alimentos);
//        return ".html";
//    }
//    
//     @GetMapping("4")
//    public String filtrarPorTipoAlimento(ModelMap modelo, @PathVariable TipoAlimento tipoAlimento){
//        
//        List<Alimento> alimentos = alimentoServicio.filtrarPorTipoAlimento(tipoAlimento);
//        modelo.put("alimentos",alimentos);
//        return ".html";
//    }
//    
//       @GetMapping("5")
//    public String filtrarPorMarca(ModelMap modelo, @PathVariable String marca){
//        
//        List<Alimento> alimentos = alimentoServicio.filtrarPorMarca(marca);
//        modelo.put("alimentos",alimentos);
//        return ".html";
//    }

