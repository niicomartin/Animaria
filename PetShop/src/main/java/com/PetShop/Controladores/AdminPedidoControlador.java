package com.PetShop.Controladores;

import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.EstadoCompra;
import com.PetShop.Servicios.CompraServicio;
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

@Controller
@RequestMapping("/admin/pedidos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPedidoControlador {

    @Autowired
    private CompraServicio compraServicio;

    @GetMapping("")
    public String listar(ModelMap modelo, @RequestParam(required = false) EstadoCompra estado) {
        List<Compra> compras = compraServicio.buscarPorEstado(estado);
        modelo.put("compras", compras);
        modelo.put("estados", EstadoCompra.values());
        modelo.put("estadoSeleccionado", estado);
        return "admin-pedidos.html";
    }

    @PostMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable String id,
            @RequestParam EstadoCompra estado,
            @RequestParam(required = false) String notaAdmin) throws Exception {
        compraServicio.actualizarEstado(id, estado, notaAdmin);
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarPedido(@PathVariable String id,
            @RequestParam(required = false) String notaAdmin) throws Exception {
        compraServicio.cancelarPedido(id, notaAdmin);
        return "redirect:/admin/pedidos";
    }
}
