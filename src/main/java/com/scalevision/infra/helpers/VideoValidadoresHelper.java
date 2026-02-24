package com.scalevision.infra.helpers;

import com.scalevision.infra.exceptions.ex.DuplicateResourceException;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class VideoValidadoresHelper {

    private final VideoRespository videoRespository;

    public void validarArchivo(MultipartFile archivo) {
        String extension = archivo.getOriginalFilename().toLowerCase();

        if (!extension.endsWith(".mp4") && !extension.endsWith(".mpg") && !extension.endsWith(".mpeg"){
            throw new ValidationExce("El Formato no es compatible");
        }
        if (archivo.getSize() > 157286400) {
            throw new ValidateException("El video excede los 150mb");
        }
        if (videoRespository.findByNombreAndActivoTrue(archivo.getName()).isPresent()) {
            throw new DuplicateResourceException("El Video " + nombre + " ya existente!");
        }
    }

    public void validaVideoExiste(String nombre) {

        videoRespository.findByNombreAndActivoTrue(nombre)
                .ifPresent(video -> {
                    throw new DuplicateResourceException("El Video " + nombre + " ya existente!");
                });

    }

}
