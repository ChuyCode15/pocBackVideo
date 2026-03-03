package com.scalevision.services;


import com.scalevision.domain.video.Video;
import com.scalevision.domain.video.dto.*;
import com.scalevision.enums.VideoStatus;
import com.scalevision.infra.clients.slicer.SlicerClient;
import com.scalevision.infra.clients.slicer.dto.DatosSlicerRequest;
import com.scalevision.infra.clients.slicer.dto.DatosSlicerStatus;
import com.scalevision.infra.clients.slicer.dto.SujetoDTO;
import com.scalevision.infra.exceptions.ex.ResourceNotFoundException;
import com.scalevision.infra.helpers.VideoValidadoresHelper;
import com.scalevision.repository.VideoRespository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static com.scalevision.enums.VideoStatus.*;

@Service
@RequiredArgsConstructor
public class VideoService {

    @Value("${app.storage.path}")
    private String storagePath;

    @Value("${app.storage.url}")
    private String storageUrl;

    private final VideoRespository videoRepository;

    private final VideoValidadoresHelper videoValidadoresHelper;

    private final SlicerClient slicerClient;

    public DatosDetalleVideoCargado registrarNuevoVideo(MultipartFile file, String modoCorte) {
        // 1. Validaciones
        var duration = videoValidadoresHelper.validaDuracionVideo(file);
        var extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        var formatoVideo = videoValidadoresHelper.validaFormatValido(extension);
        var nuevoNombreVideo = videoValidadoresHelper.validaCrearNombre(formatoVideo);

        // 2. Construcción de URL y Guardado
        String urlPublica = storageUrl.endsWith("/") ? storageUrl + nuevoNombreVideo : storageUrl + "/" + nuevoNombreVideo;
        guardarFisicamenteVideo(file, nuevoNombreVideo);

        // 3. Persistencia
        Video video = new Video();
        video.setNombre(nuevoNombreVideo);
        video.setUrlVideoOriginal(urlPublica);
        video.setModoCorte(modoCorte);
        video.setDuracion(duration);
        video.setEstado(SUBIDO);
        var videoRegistro = videoRepository.save(video);

        //  enviar video a AI Listo
        try {
            var respuesta = slicerClient.enviarPeticion(new DatosSlicerRequest(videoRegistro));

            videoRegistro.setEstado(PROCESANDO);
            videoRepository.save(videoRegistro);

        } catch (RuntimeException e) {
            videoRegistro.setEstado(ERROR);
            videoRepository.save(videoRegistro);
            throw new RuntimeException("error enviando video al procesador: " + e.getMessage());
        }

        return new DatosDetalleVideoCargado(videoRegistro);
    }

    public DatosDetalleVideoEstado verificarEstado(UUID id) {
        var video = videoValidadoresHelper.validaVideoExista(id);

        try {
            var consulta = slicerClient.estadoScaneo(id);
            if (consulta == null) return new DatosDetalleVideoEstado(video);

            // 1. Siempre actualizamos el mensaje (para que el Front sepa qué hace la IA)
            video.setMensaje(consulta.message());

            // 2. Si sigue trabajando (Status 202 en su Python), devolvemos lo que hay
            if ("PROCESANDO".equals(consulta.status())) {
                return new DatosDetalleVideoEstado(video);
            }

            // 3. Si ya es PROCESADO, guardamos los resultados finales
            if ("PROCESADO".equals(consulta.status())) {
                video.setEstado(VideoStatus.CORTADO);


                // Si hay "personas" (subjects), las guardamos
                if (consulta.sujetos() != null && !consulta.sujetos().isEmpty()) {
                    mapearMiniaturas(video, consulta.sujetos());
                }
            }

            videoRepository.save(video);

        } catch (Exception e) {
            // Si ella manda un 404 o 500, capturamos el error sin que muera tu sistema
            video.setMensaje("Error de comunicación con IA");
        }

        return new DatosDetalleVideoEstado(video);
    }

    public DatosDetalleVideoMiniVistas miniVistasService(UUID id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        return new DatosDetalleVideoMiniVistas(videoEncontrado);
    }

    public DatosDetalleVideo buscarVideoRecortado(UUID id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        return new DatosDetalleVideo(videoEncontrado);
    }

    @Transactional
    public DatosDetalleVideoEstado cortarVideo(UUID id, DatosDetalleVideoCortar corte) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        // todo llamaos micor servicio y enviamos id , url solicitamos corte, confirmamos cortado
        videoEncontrado.setEstado(CORTANDO);
        return new DatosDetalleVideoEstado(videoEncontrado);
    }

    public Page<DatosDetalleVideoLista> listaVideosDisponibles(Pageable pageable) {

        return videoRepository.findAllByActivoTrue(pageable)
                .map(
                        video -> new DatosDetalleVideoLista(
                                video.getId(),
                                video.getUrlVideoOriginal(),
                                video.getUrlVideoNuevo(),
                                video.getEstado().toString()
                        )
                );
    }

    @Transactional
    public DatosDetalleVideo eliminarVideo(UUID id) {
        var videoEncontrado = videoValidadoresHelper.validaVideoExista(id);
        eliminarFisicamenteVideo(videoEncontrado.getNombre());
        videoEncontrado.setEstado(ELIMINADO);
        videoEncontrado.setActivo(false);
        return new DatosDetalleVideo(videoEncontrado);
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

    private void eliminarFisicamenteVideo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            throw new ResourceNotFoundException("No! se puede eliminar el nombre del archivo esta vacio.");
        }
        try {
            Path rutaVideo = Paths.get(storagePath).resolve(nombreArchivo).toAbsolutePath().normalize();
            if (!Files.exists(rutaVideo)) {
                throw new ResourceNotFoundException("El archivo no existe: " + nombreArchivo + "en el servidor.");
            }
            Files.delete(rutaVideo);
        } catch (IOException e) {
            throw new ResourceNotFoundException("Error al eliminar el video: " + e.getMessage());
        }
    }

    private void mapearMiniaturas(Video video, List<SujetoDTO> sujetos) {
        // Solo guardamos lo que alcancemos (máximo 3)
        if (sujetos.size() > 0) video.setUrlMiniVista01(sujetos.get(0).url());
        if (sujetos.size() > 1) video.setUrlMiniVista02(sujetos.get(1).url());
        if (sujetos.size() > 2) video.setUrlMiniVista03(sujetos.get(2).url());
    }



}
