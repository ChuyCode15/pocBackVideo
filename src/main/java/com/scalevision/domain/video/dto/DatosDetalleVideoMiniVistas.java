package com.scalevision.domain.video.dto;

import com.scalevision.domain.video.Video;

import java.util.UUID;


public record DatosDetalleVideoMiniVistas(

        UUID id, // genera solo
        String urlMiniVista01, // genrada por modelo de video
        String urlMiniVista02, // ""
        String urlMiniVista03 // ""

) {
    public DatosDetalleVideoMiniVistas (Video video){
        this(
                video.getId(),
                video.getUrlMiniVista01(),
                video.getUrlMiniVista02(),
                video.getUrlMiniVista03()
        );
    }
}
