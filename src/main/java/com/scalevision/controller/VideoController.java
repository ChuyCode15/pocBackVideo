package com.scalevision.controller;

import com.scalevision.domain.video.dto.DatosDetalleVideo;
import com.scalevision.domain.video.dto.DatosRegistroVideo;
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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<DatosDetalleVideo> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "duration", required = false) Double duration,
            UriComponentsBuilder builder
    ) {

        DatosDetalleVideo nuevoVideo = videoService.nuevoVideo(file, nickname, duration);

        var uri = builder.path("/videos/{id}").buildAndExpand(nuevoVideo.id()).toUri();

        return ResponseEntity.created(uri).body(nuevoVideo);
    }



}


