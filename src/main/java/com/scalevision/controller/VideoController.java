package com.scalevision.controller;

import com.scalevision.domain.video.dto.*;
import com.scalevision.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    //todo POST: URL/videos/subir

    @PostMapping(consumes = {"multipart/form-data"}, value = "/subir")
    public ResponseEntity<DatosDetalleVideoCargado> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("modo_corte")String modoCorte,
            UriComponentsBuilder builder
    )  {
        DatosDetalleVideoCargado nuevoVideo = videoService.registrarNuevoVideo(file, modoCorte );
        var uri = builder.path("/videos/{id}").buildAndExpand(nuevoVideo.id()).toUri();
        return ResponseEntity.created(uri).body(nuevoVideo);
    }

    //GET: URL/videos/estado/{id}
    @GetMapping( value = "/estado/{id}")
    public ResponseEntity<DatosDetalleVideoEstado> getVideo(@PathVariable UUID id){
        var estado = videoService.verificarEstado(id);
        return ResponseEntity.ok(estado);
    }

    //GET: URL/videos/mini-vistas/{id}
    @GetMapping(value = "/mini-vistas/{id}")
    public ResponseEntity<DatosDetalleVideoMiniVistas> videoMiniVistas(@PathVariable UUID id){
        var minivistas = videoService.miniVistasService(id);
        return ResponseEntity.ok(minivistas);
    }

    //POST :URL/videos/cortar-video/{id}
    @PostMapping(value = "cortar-video/{id}")
    public ResponseEntity<DatosDetalleVideoEstado> cortarVideo(@PathVariable UUID id, @RequestBody DatosDetalleVideoCortar corte){
        var cortarVideo = videoService.cortarVideo(id, corte);
        return ResponseEntity.ok(cortarVideo);
    }

    //GET:URL/videos/final/{id}
    @GetMapping(value = "/final/{id}")
    ResponseEntity<DatosDetalleVideo> videoFinal(@PathVariable UUID id){
        var videoEncontrado = videoService.buscarVideoRecortado(id);
        return ResponseEntity.ok(videoEncontrado);
    }

    @GetMapping
    ResponseEntity<Page<DatosDetalleVideoLista>> listarVideos(@PageableDefault( size = 10 ) Pageable pageable){
        var listaVideosDisponibles = videoService.listaVideosDisponibles(pageable);
            return ResponseEntity.ok(listaVideosDisponibles);
    }

    @DeleteMapping(value = "/eliminar-video/{id}")
    public ResponseEntity<DatosDetalleVideo> eliminarVideo(@PathVariable UUID id){
        var elimiarVideo = videoService.eliminarVideo(id);
        return ResponseEntity.ok(elimiarVideo);
    }



}


