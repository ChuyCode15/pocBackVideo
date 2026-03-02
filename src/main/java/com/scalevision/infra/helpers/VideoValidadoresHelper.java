package com.scalevision.infra.helpers;

import com.scalevision.domain.video.Video;
import com.scalevision.enums.Formato;
import com.scalevision.infra.exceptions.ex.InvalidFormatException;
import com.scalevision.infra.exceptions.ex.ResourceNotFoundException;
import com.scalevision.infra.exceptions.ex.RuleValidationException;
import com.scalevision.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VideoValidadoresHelper {

    private final VideoRespository videoRespository;


    public String validaFormatValido(String formato) {
        if (formato == null) {
            throw new InvalidFormatException("El formato no puede ser nulo");
        }
        var formatoUpper = formato.toUpperCase().trim();
        try {
            Formato.valueOf(formatoUpper); //Formato es el enum valueof compara si formatoUpper es un dato valido
            return formatoUpper;
        } catch (IllegalArgumentException e) {
            throw new InvalidFormatException("¡Formato: " + formatoUpper + " no es válido! Solo permitimos: " +
                    Arrays.toString(Formato.values()));
        }
    }

    public Integer validaDuracionVideo(MultipartFile file) {

        Path copiaValiacion = null;
        Integer duracion = 0;
        try {
            copiaValiacion = Files.createTempFile("check-", ".tmp");
            file.transferTo(copiaValiacion);
            duracion = getDuracionConFFprobe(copiaValiacion);

            if (duracion > 60 || duracion < 1) {
                throw new IllegalArgumentException("El video es demasiado largo: " + duracion + " segundos.");
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al procesar el video con ffprobe", e);
        } finally {
            if (copiaValiacion != null) {
                try {
                    Files.deleteIfExists(copiaValiacion);
                } catch (IOException ignored) {
                }
            }
        }
        return duracion;
    }

    private Integer getDuracionConFFprobe(Path videoPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
                "ffprobe", "-v", "error", "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1", videoPath.toString()
        );

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String output = reader.readLine();
            if (output == null || output.isEmpty()) {
                throw new IOException("FFprobe no devolvió ninguna duración.");
            }
            return (int) Double.parseDouble(output);
        } finally {
            if (process != null) {
                process.getInputStream().close();
                process.destroy();
                process.waitFor();
            }

        }
    }


    public String validaCrearNombre(String formato) {
        String nuevoNombre = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HHmmss-"));
        var nickName = "usuario" + (new Random().nextInt(9000) + 1000);
        return nuevoNombre + nickName + "." + formato;
    }

    public Video validaVideoExista(UUID id) {
        if (id == null) {
            return null;
        }
        var video = videoRespository.findByIdAndActivoTrue(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("⚠️ Video No encontrado ID:" + id + " no existe o es invalido.")
                );
        return video;
    }
}
