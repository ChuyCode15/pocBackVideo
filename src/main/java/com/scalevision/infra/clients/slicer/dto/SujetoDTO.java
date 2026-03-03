package com.scalevision.infra.clients.slicer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SujetoDTO(
        @JsonProperty("subject_id") String id,
        @JsonProperty("thumbnail_url") String url
) {}
