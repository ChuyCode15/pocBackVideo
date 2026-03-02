package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;

import java.util.UUID;

public record DatosDetalleVideoEstado(
        UUID id,
        String estado
) {
    public DatosDetalleVideoEstado(Video video) {
        this(
                video.getId(),
                video.getEstado().toString()
        );
    }
}
