package com.scalevision.services;

import com.scalevision.domain.video.dto.DatosDetalleVideo;
import com.scalevision.infra.helpers.VideoValidadoresHelper;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRespository videoRespository;

    private final VideoValidadoresHelper videoValidadoresHelper;

    public DatosDetalleVideo nuevoVideo(MultipartFile file) {

        videoValidadoresHelper.validaVideoExiste(file.getName());

    }
}
