package com.scalevision.domain.video;

import com.scalevision.enums.Formato;
import jakarta.persistence.*;
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
    Double duracion;  // segundos default
    String estado; //  enum ejusta el back
    String error;  // 501, se desconecto.
    LocalDateTime fecha; // cuando se genero

    @Column(name = "url_video_original")
    String urlVideoOriginal; // se genera al subir el video

    @Column(name = "url_mini_vista_01" )
    String urlMiniVista01; // genrada por modelo de video

    @Column(name = "url_mini_vista_02")
    String urlMiniVista_02; // ""

    @Column(name = "url_mini_vista_03")
    String urlMiniVista03; // ""

    @Column(name = "url_vista_selecionada")
    String urlVistaSelecionada; // se genra por el usaurio desde front

    @Column(name = "url_video_nuevo")
    String urlVideoNuevo; // ulr del video generado por el modelo de video

    Boolean activo = true ; // default true eliinacion logica
}