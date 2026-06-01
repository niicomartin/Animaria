package com.PetShop.Controladores;

import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.EstadoCompra;
import com.PetShop.Servicios.AccesorioServicio;
import com.PetShop.Servicios.AlimentoServicio;
import com.PetShop.Servicios.CompraServicio;
import com.PetShop.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardControlador {

    @Autowired private CompraServicio compraServicio;
    @Autowired private UsuarioServicio usuarioServicio;
    @Autowired private AlimentoServicio alimentoServicio;
    @Autowired private AccesorioServicio accesorioServicio;

    @GetMapping("/admin")
    public String panel(ModelMap modelo) {
        java.util.List<Compra> compras = compraServicio.buscarActivas();
        int pendientes = 0;
        int preparando = 0;
        int camino = 0;
        int entregados = 0;
        int cancelados = 0;

        for (Compra compra : compras) {
            if (compra.getEstado() == EstadoCompra.PENDIENTE) pendientes++;
            if (compra.getEstado() == EstadoCompra.PREPARANDO) preparando++;
            if (compra.getEstado() == EstadoCompra.EN_CAMINO) camino++;
            if (compra.getEstado() == EstadoCompra.ENTREGADO) entregados++;
            if (compra.getEstado() == EstadoCompra.CANCELADO) cancelados++;
        }

        modelo.put("compras", compras);
        modelo.put("cantidadPedidos", compras.size());
        modelo.put("cantidadUsuarios", usuarioServicio.getAll().size());
        modelo.put("cantidadAlimentos", alimentoServicio.listar().size());
        modelo.put("cantidadAccesorios", accesorioServicio.listar().size());
        modelo.put("pendientes", pendientes);
        modelo.put("preparando", preparando);
        modelo.put("camino", camino);
        modelo.put("entregados", entregados);
        modelo.put("cancelados", cancelados);
        return "admin-panel.html";
    }
}
