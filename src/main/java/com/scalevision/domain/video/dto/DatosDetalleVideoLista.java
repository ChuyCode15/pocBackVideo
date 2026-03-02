package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;
import jakarta.persistence.Column;

import java.util.UUID;

public record DatosDetalleVideoLista(

        //@Schema(description = "ID único del video en la base de datos", example = "1")
        UUID id,

        //@Schema(description = "URL pública para acceder al video vía Nginx", example = "http://localhost/videos/user_123_video.mp4")
        String urlVideoOriginal,

        //@Schema(description = "Estado actual del video", example = "PROCESANDO")
        String urlVideoNuevo,

        String estado

) {

    public DatosDetalleVideoLista(Video video) {
        this(
                video.getId(),
                video.getUrlVideoOriginal(),
                video.getUrlVideoNuevo(),
                video.getEstado().toString()
        );
    }

}

