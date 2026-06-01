package com.PetShop.Servicios;

import com.PetShop.Entidades.Alimento;
import com.PetShop.Entidades.Animal;
import com.PetShop.Entidades.Imagen;
import com.PetShop.Entidades.TipoAlimento;
import com.PetShop.Repositorios.AlimentoRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AlimentoServicio {

    @Autowired
    private AlimentoRepositorio alimentoRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    @Transactional(rollbackFor = Exception.class)
    public Alimento crear(Integer cantidad, Animal animal, TipoAlimento tipoAlimento, String marca, Integer stock, double precio, MultipartFile archivo) throws Exception {
        validar(cantidad, animal, tipoAlimento, marca, stock, precio);

        Alimento alimento = new Alimento();
        alimento.setCantidad(cantidad);
        alimento.setAnimal(animal);
        alimento.setTipoAlimento(tipoAlimento);
        alimento.setMarca(marca);
        alimento.setStock(stock);
        alimento.setPrecio(precio);
        alimento.setActivo(true);

        if (archivo != null && !archivo.isEmpty()) {
            Imagen imagen = imagenServicio.guardar(archivo);
            alimento.setImagen(imagen);
        }

        return alimentoRepositorio.save(alimento);
    }

    @Transactional(rollbackFor = Exception.class)
    public Alimento editar(String id, Integer cantidad, Animal animal, TipoAlimento tipoAlimento, String marca, Integer stock, double precio, boolean activo, MultipartFile archivo) throws Exception {
        validar(cantidad, animal, tipoAlimento, marca, stock, precio);

        Alimento alimento = buscarPorId(id);
        alimento.setCantidad(cantidad);
        alimento.setAnimal(animal);
        alimento.setTipoAlimento(tipoAlimento);
        alimento.setMarca(marca);
        alimento.setStock(stock);
        alimento.setPrecio(precio);
        alimento.setActivo(activo);

        if (archivo != null && !archivo.isEmpty()) {
            Imagen imagen = imagenServicio.guardar(archivo);
            alimento.setImagen(imagen);
        }

        return alimentoRepositorio.save(alimento);
    }

    @Transactional(readOnly = true)
    public List<Alimento> listar() {
        return alimentoRepositorio.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Alimento> listarTodos() {
        return alimentoRepositorio.findAll();
    }

    @Transactional(readOnly = true)
    public List<Alimento> filtrarPorAnimal(String animal) {
        if (animal == null || animal.trim().isEmpty()) {
            return listar();
        }
        return alimentoRepositorio.findByActivoTrueAndAnimal(Animal.valueOf(animal));
    }

    @Transactional(readOnly = true)
    public List<Alimento> filtrarPorMarca(String marca) {
        return alimentoRepositorio.findByActivoTrueAndMarcaContainingIgnoreCase(marca);
    }

    @Transactional(readOnly = true)
    public List<Alimento> filtrar(String tipoAlimento, String animal) {
        if (animal == null || animal.trim().isEmpty()) {
            return listar();
        }
        if (tipoAlimento == null || tipoAlimento.trim().isEmpty()) {
            return filtrarPorAnimal(animal);
        }
        return alimentoRepositorio.findByActivoTrueAndTipoAlimentoAndAnimal(TipoAlimento.valueOf(tipoAlimento), Animal.valueOf(animal));
    }

    @Transactional(rollbackFor = Exception.class)
    public Alimento darBaja(String id) throws Exception {
        Alimento alimento = buscarPorId(id);
        alimento.setActivo(false);
        return alimentoRepositorio.save(alimento);
    }

    @Transactional(rollbackFor = Exception.class)
    public void anular(String id) throws Exception {
        Alimento alimento = buscarPorId(id);
        alimento.setActivo(!alimento.isActivo());
        alimentoRepositorio.save(alimento);
    }

    @Transactional(readOnly = true)
    public Alimento buscarPorId(String id) throws Exception {
        if (id == null || id.trim().isEmpty()) {
            throw new Exception("ID de alimento inválido");
        }

        Optional<Alimento> respuesta = alimentoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            return respuesta.get();
        }

        throw new Exception("No se encuentra el alimento con ese id");
    }

    private void validar(Integer cantidad, Animal animal, TipoAlimento tipoAlimento, String marca, Integer stock, double precio) throws Exception {
        if (cantidad == null || cantidad <= 0) {
            throw new Exception("La cantidad debe ser mayor a cero");
        }
        if (animal == null) {
            throw new Exception("La categoría del animal es obligatoria");
        }
        if (tipoAlimento == null) {
            throw new Exception("El tipo de alimento es obligatorio");
        }
        if (marca == null || marca.trim().isEmpty()) {
            throw new Exception("La marca es obligatoria");
        }
        if (stock == null || stock < 0) {
            throw new Exception("El stock no puede estar vacío ni ser negativo");
        }
        if (precio <= 0) {
            throw new Exception("El precio debe ser mayor a cero");
        }
    }
}
