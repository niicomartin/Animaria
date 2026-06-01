package com.PetShop.Repositorios;

import com.PetShop.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);

    @Query("SELECT u FROM Usuario u WHERE u.tokenVerificacion = :token")
    public Usuario buscarPorTokenVerificacion(@Param("token") String token);

    @Query("SELECT u FROM Usuario u WHERE u.tokenRecuperacion = :token")
    public Usuario buscarPorTokenRecuperacion(@Param("token") String token);
}
