package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;

public record DatosDetalleEstado(
        Long id,
        String estado
) {
    public  DatosDetalleEstado(Video video) {
        this(
                video.getId(),
                video.getEstado().toString()
        );
    }
}
