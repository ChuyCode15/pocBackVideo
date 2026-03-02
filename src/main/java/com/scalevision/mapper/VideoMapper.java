package com.scalevision.mapper;

import com.scalevision.domain.video.Video;
import com.scalevision.infra.clients.slicer.dto.DatosSlicerRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    @Mapping(source = "urlVideoOriginal", target = "videoUrl")
    @Mapping(source = "modoCorte", target = "modoCorte")
    DatosSlicerRequest videoToSlicerRequest(Video video);
}
