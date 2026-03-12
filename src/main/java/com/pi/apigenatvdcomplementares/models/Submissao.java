package com.pi.apigenatvdcomplementares.models;

import java.time.LocalDateTime;
import java.util.Set;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_submissoes")
public class Submissao extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "submissao_id")
  private Long id;

  @Column(name = "data_submissao", nullable = false)
  private LocalDateTime dataSubmissao;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_submissao", nullable = false, length = 50)
  private StatusSubmissao status;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "tb_historico_status_submissao", joinColumns = @JoinColumn(name = "submissao_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "historico_status")
  private Set<StatusSubmissao> historicoStatus;

  @OneToMany
  @JoinColumn(name = "certificado_id")
  private Set<Certificado> certificados;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "aluno_id", nullable = false)
  private Aluno aluno;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "curso_id", nullable = false)
  private Curso curso;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coordenador_id")
  private Usuario coordenador;
}
