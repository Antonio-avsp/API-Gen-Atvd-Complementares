package com.pi.apigenatvdcomplementares.models;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_coordenadores_curso")
public class CoordenadorCurso extends Auditable {

  @ManyToOne(fetch = FetchType.LAZY) 
  @JoinColumn(name = "coordenador_id", nullable = false)
  private Usuario coordenadorId;

  @ManyToOne(fetch = FetchType.LAZY) // 
  @JoinColumn(name = "curso_id", nullable = false)
  private Curso cursoId;




}
