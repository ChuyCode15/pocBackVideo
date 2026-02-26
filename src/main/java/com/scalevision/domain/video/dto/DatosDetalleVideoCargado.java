package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;

public record DatosDetalleVideoCargado(
        //@Schema(description = "ID único del video en la base de datos", example = "1")
        Long id,

        //@Schema(description = "URL pública para acceder al video vía Nginx", example = "http://localhost/videos/user_123_video.mp4")
        String url,

        //@Schema(description = "Estado actual del video", example = "PROCESANDO")
        String estado
) {
    public DatosDetalleVideoCargado(Video video) {
        this(
                video.getId(),
                video.getUrlVideoOriginal(),
                video.getEstado().toString()
        );
    }
}

