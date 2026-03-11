package com.pi.apigenatvdcomplementares.models;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

public class RegraAtividade extends Auditable {

  @Id
  @Column(name = "regra_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  @Column(name= "curso_id", nullable = false)
  
  private Curso cursoId;

  @Column(name = "area", nullable = false, length = 255)
  private String area;

  @Column(name = "limite_horas", nullable = false)
  private int limiteHoras;

  @Column(name = "exige_comprovante", nullable = false)
  private boolean exigeComprovante;

  @ManyToOne(fetch = FetchType.LAZY)
  private Curso curso;
    
}
