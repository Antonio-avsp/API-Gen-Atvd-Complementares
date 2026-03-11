package com.pi.apigenatvdcomplementares.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_coordenadores_curso")
public class CoordenadorCurso extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY) 
  @JoinColumn(name = "coordenador_id", nullable = false)
  private Usuario coordenadorId;

  @ManyToOne(fetch = FetchType.LAZY) // 
  @JoinColumn(name = "curso_id", nullable = false)
  private Curso cursoId;



}
