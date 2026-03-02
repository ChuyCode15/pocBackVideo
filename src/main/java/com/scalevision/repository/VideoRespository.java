package com.scalevision.repository;

import com.scalevision.domain.video.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VideoRespository extends JpaRepository<Video, UUID> {

    Optional<Video> findByNombreAndActivoTrue(String nombre);

    Optional<Video> findByIdAndActivoTrue(UUID id);

    Page<Video> findAllByActivoTrue(Pageable pageable);
}
