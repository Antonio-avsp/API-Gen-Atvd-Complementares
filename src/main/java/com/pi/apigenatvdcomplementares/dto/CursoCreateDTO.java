package com.pi.apigenatvdcomplementares.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CursoCreateDTO {

  private String nome;
  private boolean statusCurso;
  private int cargaHorariaMinima;

}
