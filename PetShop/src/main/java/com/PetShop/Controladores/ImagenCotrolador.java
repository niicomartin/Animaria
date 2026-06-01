package com.PetShop.Controladores;

import com.PetShop.Entidades.Accesorio;
import com.PetShop.Entidades.Alimento;
import com.PetShop.Servicios.AccesorioServicio;
import com.PetShop.Servicios.AlimentoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ImagenCotrolador {

    @Autowired
    private AlimentoServicio alimentoServicio;

    @Autowired
    private AccesorioServicio accesorioServicio;

    @GetMapping("/imagen/alimento/{id}")
    public ResponseEntity<byte[]> imagenAlimento(@PathVariable String id) {

        try {
            Alimento alimento = alimentoServicio.buscarPorId(id);
            byte[] imagen = alimento.getImagen().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } catch (Exception ex) {

        }

        return null;
    }

    @GetMapping("/imagen/accesorio/{id}")
    public ResponseEntity<byte[]> accesorioAlimento(@PathVariable String id) {

        try {
            Accesorio accesorio = accesorioServicio.buscarPorId(id);
            byte[] imagen = accesorio.getImagen().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imagen, headers, HttpStatus.OK);
        } catch (Exception ex) {

        }

        return null;
    }

}
