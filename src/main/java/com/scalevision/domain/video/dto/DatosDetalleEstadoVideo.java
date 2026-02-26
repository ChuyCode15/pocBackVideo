package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;

public record DatosDetalleEstadoVideo(
        Long id,
        String estado
) {
    public DatosDetalleEstadoVideo(Video video) {
        this(
                video.getId(),
                video.getEstado().toString()
        );
    }
}
