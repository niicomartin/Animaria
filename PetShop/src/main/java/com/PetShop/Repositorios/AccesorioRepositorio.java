
package com.PetShop.Repositorios;

import com.PetShop.Entidades.Accesorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccesorioRepositorio extends JpaRepository<Accesorio, String> {


}
