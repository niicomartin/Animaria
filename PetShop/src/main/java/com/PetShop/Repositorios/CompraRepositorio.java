package com.PetShop.Repositorios;

import com.PetShop.Entidades.Compra;
import com.PetShop.Entidades.EstadoCompra;
import com.PetShop.Entidades.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepositorio extends JpaRepository<Compra, String> {

    @Query("SELECT c FROM Compra c WHERE c.alta = true ORDER BY c.fechaAlta DESC")
    public List<Compra> buscarActivas();

    public List<Compra> findByUsuarioOrderByFechaAltaDesc(Usuario usuario);

    public List<Compra> findByEstadoOrderByFechaAltaDesc(EstadoCompra estado);
}
