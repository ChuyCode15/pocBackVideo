package com.scalevision.infra.helpers;

import com.scalevision.infra.exceptions.ex.DuplicateResourceException;
import com.scalevision.infra.exceptions.ex.InvalidFormatException;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VideoValidadoresHelper {

    private final VideoRespository videoRespository;


    public void validaVideoExiste(String nombre) {

        videoRespository.findByNombreAndActivoTrue(nombre)
                .ifPresent(video -> {
                    throw new DuplicateResourceException("El Video " + nombre + " ya existente!");
                });

    }

    public String validaFormatValido(String formato) {
        if (formato == null ||
                !formato.equalsIgnoreCase("mp4") ||
                formato.equalsIgnoreCase("mpg") ||
                formato.equalsIgnoreCase("mpeg")) {
            throw new InvalidFormatException("Formato invalido!");
        }
        return formato;
    }

    public void validaDuracionValida(Double duration) {

        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duración inválida");
        }

    }

    public String validaNickNameYCreaNombre(String nickName, String formato) {
        String nuevoNombre = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss-"));

        if (nickName == null || nickName.isBlank()) {
            nickName = "usuario" + (new Random().nextInt(9000) + 1000);
        }

        return nuevoNombre + nickName + "." + formato;
    }
}
