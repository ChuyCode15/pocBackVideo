package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "DetalleVideo",
        description = "Representa la información final de un video procesado dentro de ScaleVision"
)
public record DatosDetalleVideo(

        @Schema(
                description = "Identificador único del video en la base de datos",
                example = "1",
                accessMode = Schema.AccessMode.READ_ONLY
        )
        UUID id,

        @Schema(
                description = "URL pública para acceder al video procesado",
                example = "http://localhost/media/videos/video_recortado_1.mp4"
        )
        String url,

        @Schema(
                description = "Estado actual del procesamiento del video",
                example = "COMPLETADO",
                allowableValues = {"PENDIENTE", "PROCESANDO", "COMPLETADO", "ERROR"}
        )
        String estado
) {
    public DatosDetalleVideo(Video video) {
        this(
                video.getId(),
                video.getUrlVideoNuevo(),
                video.getEstado().toString()
        );
    }
}