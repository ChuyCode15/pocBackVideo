package com.scalevision.infra.helpers;

import com.scalevision.domain.video.Video;
import com.scalevision.domain.video.dto.DatosDetalleVideo;
import com.scalevision.enums.Formato;
import com.scalevision.infra.exceptions.ex.InvalidFormatException;
import com.scalevision.infra.exceptions.ex.ResourceNotFoundException;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VideoValidadoresHelper {

    private final VideoRespository videoRespository;


    public String validaFormatValido(String formato) {
        if (formato == null) {
            throw new InvalidFormatException("El formato no puede ser nulo");
        }

        String formatoUpper = formato.toUpperCase().trim();

        try {
            Formato.valueOf(formatoUpper);
            return formatoUpper;
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException("¡Formato " + formatoUpper + " no es válido! Solo permitimos: " +
                    Arrays.toString(Formato.values()));
        }
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

    public Video validaVideoExista(Long id) {
        if ( id == null ){
            return null;
        }
        var video = videoRespository.findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("⚠️ Video No encontrado ID:" + id + " no existe o es invalido.")
                );
        return video;
    }
}
