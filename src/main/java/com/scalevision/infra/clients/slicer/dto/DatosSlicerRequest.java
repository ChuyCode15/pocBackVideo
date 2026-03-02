package com.scalevision.infra.clients.slicer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scalevision.domain.video.Video;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record DatosSlicerRequest(

        @Schema(description = "ID único del video (UUID)", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,

        @JsonProperty("video_url")
        @Schema(description = "URL pública del video en el storage", example = "https://tu-storage.com/video123.mp4")
        String videoUrl,

        @JsonProperty("modo_corte")
        @Schema(description = "Estrategia de corte a aplicar", example = "dynamic", allowableValues = {"dynamic", "fixed", "manual"})
        String modoCorte
) {
    public DatosSlicerRequest(Video video) {

        this(
                video.getId(),
                video.getUrlVideoOriginal(),
                video.getModoCorte()
        );

    }
}
