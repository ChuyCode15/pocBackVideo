package com.scalevision.services;


import com.scalevision.domain.video.Video;
import com.scalevision.domain.video.dto.DatosDetalleVideo;
import com.scalevision.infra.helpers.VideoValidadoresHelper;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${app.storage.path}")
    private String storagePath;

    @Value("${app.storage.url}")
    private String storageUrl;

    private final VideoRespository videoRespository;

    private final VideoValidadoresHelper videoValidadoresHelper;

    public DatosDetalleVideo nuevoVideo(MultipartFile file, String nickname, Double duration) {
        // 1. Validaciones
        videoValidadoresHelper.validaDuracionValida(duration);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        var formatoVideo = videoValidadoresHelper.validaFormatValido(extension);
        var nuevoNombreVideo = videoValidadoresHelper.validaNickNameYCreaNombre(nickname, formatoVideo);

        // 2. Construcción de URL y Guardado
        String urlPublica = storageUrl.endsWith("/") ? storageUrl + nuevoNombreVideo : storageUrl + "/" + nuevoNombreVideo;
        guardarFisicamente(file, nuevoNombreVideo);

        // 3. Persistencia
        Video video = new Video();
        video.setNombre(nuevoNombreVideo);
        video.setUrlVideoNuevo(urlPublica);
        video.setDuracion(duration);
        video.setNickname(nickname);

        var guardado = videoRespository.save(video);

        return new DatosDetalleVideo(guardado.getId(), guardado.getNombre(), guardado.getUrl(), guardado.getDuracion());
    }

    private void guardarFisicamente(MultipartFile file, String nombreArchivo) {
        try {
            // Paths.get maneja correctamente los separadores según el SO (Win/Linux)
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


    public void guardarVideo(MultipartFile file, String nuevoNombreVideo) {
        Path ruta = Paths.get(storagePath, nuevoNombreVideo);
        Files.copy(file.getInputStream(), ruta, StandardCopyOption.REPLACE_EXISTING)
                .orElseTwors(
                        () -> FileUploadException("No se pudo fallo el serviso storage o algo pero el vidoe no se guardo ")
                );
    }

}
