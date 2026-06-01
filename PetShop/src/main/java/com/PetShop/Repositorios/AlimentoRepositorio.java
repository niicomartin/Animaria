package com.PetShop.Repositorios;

import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Animal;
import com.PetShop.Entidades.TipoAlimento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlimentoRepositorio extends JpaRepository<Alimento, String> {

    public List<Alimento> findByActivoTrue();

    public List<Alimento> findByActivoTrueAndAnimal(Animal animal);

    public List<Alimento> findByActivoTrueAndMarcaContainingIgnoreCase(String marca);

    public List<Alimento> findByActivoTrueAndTipoAlimentoAndAnimal(TipoAlimento tipoAlimento, Animal animal);
}
