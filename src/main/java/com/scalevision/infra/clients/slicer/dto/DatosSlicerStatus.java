package com.scalevision.infra.clients.slicer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSlicerStatus(
        String status,
        String message,
        @JsonProperty("subjects") List<SujetoDTO> sujetos,
        FallbackDTO fallback
) {}

@JsonIgnoreProperties(ignoreUnknown = true)
record FallbackDTO(
        @JsonProperty("is_active") Boolean isActive,
        String strategy
) {}