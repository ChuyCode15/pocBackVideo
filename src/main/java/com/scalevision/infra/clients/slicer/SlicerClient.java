package com.scalevision.infra.clients.slicer;

import com.scalevision.infra.clients.slicer.dto.DatosSlicerRequest;
import com.scalevision.infra.clients.slicer.dto.DatosSlicerResposeSubir;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Component
public class SlicerClient {

    private final RestClient slicerClient;

    public SlicerClient(@Value("${SLICER_API_URL}") String slicerApiUrl) {

        HttpClient motorHttp = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.slicerClient = RestClient.builder()
                .baseUrl(slicerApiUrl)
                .requestFactory(new JdkClientHttpRequestFactory(motorHttp))
                .build();
    }


    public DatosSlicerResposeSubir enviarPeticion(DatosSlicerRequest datos) {

        try {
            return slicerClient.post()
                    .uri("/scan")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(datos)
                    .retrieve()
                    .body(DatosSlicerResposeSubir.class);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo conectar con el servicio Slicer");
        }

    }


}
