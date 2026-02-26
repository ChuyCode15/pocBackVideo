package com.scalevision.controller;

import com.scalevision.domain.video.dto.*;
import com.scalevision.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    //todo POST: URL/videos/subir
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DatosDetalleVideoCargado> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "duration", required = false) Double duration,
            UriComponentsBuilder builder
    ) {

        DatosDetalleVideoCargado nuevoVideo = videoService.registrarNuevoVideo(file, nickname, duration);
        var uri = builder.path("/videos/{id}").buildAndExpand(nuevoVideo.id()).toUri();
        return ResponseEntity.created(uri).body(nuevoVideo);
    }

    //GET: URL/videos/estado/{id}
    @GetMapping( value = "/estado/{id}")
    public ResponseEntity<DatosDetalleEstadoVideo> getVideo(@PathVariable Long id){
        var estado = videoService.verificarEstado(id);
        return ResponseEntity.ok(estado);
    }

    //GET: URL/videos/mini-vistas/{id}
    @GetMapping(value = "/mini-vistas/{id}")
    public ResponseEntity<DatosDetalleVideoMiniVistas> videoMiniVistas(@PathVariable Long id){
        var minivistas = videoService.miniVistasService(id);
        return ResponseEntity.ok(minivistas);
    }

    //POST :URL/videos/cortar-video/{id}
    @PostMapping(value = "cortar-video/{id}")
    public ResponseEntity<DatosDetalleEstadoVideo> cortarVideo(@PathVariable Long id, @RequestBody DatosDetalleCortarVideo corte){
        var cortarVideo = videoService.cortarVideo(id, corte);
        return ResponseEntity.ok(cortarVideo);
    }

    //GET:URL/videos/final/{id}
    @GetMapping(value = "/final/{id}")
    ResponseEntity<DatosDetalleVideo> videoFinal(@PathVariable Long id){
        var videoEncontrado = videoService.buscarVideoRecortado(id);
        return ResponseEntity.ok(videoEncontrado);
    }



}


