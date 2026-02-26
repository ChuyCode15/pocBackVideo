package com.scalevision.services;


import com.scalevision.domain.video.Video;
import com.scalevision.domain.video.dto.*;
import com.scalevision.infra.helpers.VideoValidadoresHelper;
import com.scalevision.repository.VideoRespository;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.scalevision.enums.VideoStatus.CORTANDO;
import static com.scalevision.enums.VideoStatus.PROCESANDO;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${app.storage.path}")
    private String storagePath;

    @Value("${app.storage.url}")
    private String storageUrl;

    private final VideoRespository videoRespository;

    private final VideoValidadoresHelper videoValidadoresHelper;

    public DatosDetalleVideoCargado registrarNuevoVideo(MultipartFile file, String nickname, Double duration) {
        // 1. Validaciones
        videoValidadoresHelper.validaDuracionValida(duration);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        var formatoVideo = videoValidadoresHelper.validaFormatValido(extension);
        var nuevoNombreVideo = videoValidadoresHelper.validaNickNameYCreaNombre(nickname, formatoVideo);

        // 2. Construcción de URL y Guardado
        String urlPublica = storageUrl.endsWith("/") ? storageUrl + nuevoNombreVideo : storageUrl + "/" + nuevoNombreVideo;
        guardarFisicamenteVideo(file, nuevoNombreVideo);

        // 3. Persistencia
        Video video = new Video();
        video.setNombre(nuevoNombreVideo);
        video.setUrlVideoNuevo(urlPublica);
        video.setDuracion(duration);
        video.setNickname(nickname);
        video.setEstado(PROCESANDO);

        var videoRegistro = videoRespository.save(video);

        return new DatosDetalleVideoCargado(videoRegistro);
    }

    private void guardarFisicamenteVideo(MultipartFile file, String nombreArchivo) {
        try {
            Path rutaDirectorio = Paths.get(storagePath).toAbsolutePath().normalize();

            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            Path destino = rutaDirectorio.resolve(nombreArchivo);
            Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el video en el disco: " + e.getMessage());
        }
    }

    public DatosDetalleEstadoVideo verificarEstado(Long id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        return new DatosDetalleEstadoVideo(videoEncontrado);
    }

    public DatosDetalleVideoMiniVistas miniVistasService(Long id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        return new DatosDetalleVideoMiniVistas(videoEncontrado);
    }

    public DatosDetalleVideo buscarVideoRecortado(Long id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        return new DatosDetalleVideo(videoEncontrado);
    }

    @Transactional
    public DatosDetalleEstadoVideo cortarVideo(Long id, DatosDetalleCortarVideo corte) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        // todo llamaos micor servicio y enviamos id , url solicitamos corte, confirmamos cortado
        videoEncontrado.setEstado(CORTANDO);
        return new DatosDetalleEstadoVideo(videoEncontrado);
    }
}
