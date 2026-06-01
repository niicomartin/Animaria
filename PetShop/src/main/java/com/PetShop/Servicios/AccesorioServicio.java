package com.PetShop.Servicios;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Entidades.Imagen;
import com.PetShop.Repositorios.AccesorioRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AccesorioServicio {

    @Autowired
    private AccesorioRepositorio accesorioRepositorio;

    @Autowired
    public ImagenServicio imagenServicio;

    @Transactional(rollbackFor = {Exception.class})
    public Accesorio crear(String marca, Integer stock, String nombre, Long precio, MultipartFile archivo) {

        Accesorio accesorio = new Accesorio();
        accesorio.setActivo(true);
        accesorio.setMarca(marca);
        accesorio.setNombre(nombre);
        accesorio.setPrecio(precio);
        accesorio.setStock(stock);
        Imagen imagen = imagenServicio.guardar(archivo);
        accesorio.setImagen(imagen);

        return accesorioRepositorio.save(accesorio);

    }

    @Transactional(readOnly = true)
    public Accesorio buscarPorId(String id) throws Exception {
        Optional<Accesorio> respuesta = accesorioRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Accesorio accesorio = respuesta.get();
            return accesorio;

        } else {
            throw new Exception("No existe ese autor");
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public Accesorio editar(String id, String marca, Integer stock, String nombre, Long precio, MultipartFile archivo) throws Exception {
        Accesorio accesorio = buscarPorId(id);
        if (marca != null) {
            accesorio.setMarca(marca);
        }
        if (stock != null) {
            accesorio.setStock(stock);
        }
        if (nombre != null) {
            accesorio.setNombre(nombre);
        }
        if (precio != null) {
            accesorio.setPrecio(precio);
        }
        if (archivo != null) {
            Imagen imagen = imagenServicio.guardar(archivo);
            accesorio.setImagen(imagen);
        }

        accesorioRepositorio.save(accesorio);

        return accesorio;
    }

    @Transactional(readOnly = true)
    public List<Accesorio> listar() {
        List<Accesorio>accesoriosAlta = accesorioRepositorio.findAll();
        List<Accesorio>accesorios = new ArrayList();
        for (Accesorio accesorio : accesoriosAlta) {
            if (accesorio.isActivo()) {
                accesorios.add(accesorio);
                
            }
            
        }
        
        return accesorios;

    }

    @Transactional(rollbackFor = {Exception.class})
    public Accesorio darBaja(Accesorio accesorio) throws Exception {
        
        accesorio.setActivo(false);
        return accesorio;

    }

}
