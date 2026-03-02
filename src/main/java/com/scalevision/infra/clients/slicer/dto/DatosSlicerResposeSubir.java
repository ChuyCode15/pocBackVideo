package com.scalevision.infra.clients.slicer.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosSlicerResposeSubir(

  @JsonAlias("estado")
  String status,

  @JsonAlias("mensaje")
  String message


) {
}
