package com.scalevision.repository;

import com.scalevision.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoRespository extends JpaRepository<Video, Long> {

    Optional<Video> findByNombreAndActivoTrue(String nombre);
}
