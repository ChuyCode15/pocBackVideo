package com.scalevision.domain.video.dto;

//import io.swagger.v3.oas.annotations.media.Schema;
import com.scalevision.domain.video.Video;

//@Schema(description = "Detalles del video procesado y guardado")
public record DatosDetalleVideo(

        //@Schema(description = "ID único del video en la base de datos", example = "1")
        Long id,

        //@Schema(description = "URL pública para acceder al video vía Nginx", example = "http://localhost/videos/user_123_video.mp4")
        String url,

        //@Schema(description = "Estado actual del video", example = "PROCESANDO")
        String estado
) {
    // Constructor compacto para mapear desde la entidad fácilmente
    public DatosDetalleVideo(Video video) {
        this(
                video.getId(),
                video.getUrlVideoNuevo(),
                video.getEstado().toString()
        );
    }
}