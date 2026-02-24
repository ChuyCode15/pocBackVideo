package com.scalevision.domain.video;

import com.scalevision.enums.Formato;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Table(name = "videos")
@Entity

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; // genera solo

    String nombre;  // se envia el original y se cambia al gaurdar formato ("2026-23-02-0837-nickaname.format")

    String nickname; // optional lo ingresa el cliente
    Double tamano;  // mb /default
    Formato formato; // mp4, mpg, mpeg /dedault
    Integer duracion;  // segundos default
    String estado; //  enum ejusta el back
    String error;  // 501, se desconecto.
    LocalDateTime fecha; // cuando se genero
    String url_video_original; // se genera al subir el video
    String Url_mini_vista_01; // genrada por modelo de video
    String Url_mini_vista_02; // ""
    String Url_mini_vista_03; // ""
    String Url_vista_selecionada; // se genra por el usaurio desde front
    String Url_video_nuevo; // ulr del video generado por el modelo de video
    Boolean activo = true ; // default true eliinacion logica
}