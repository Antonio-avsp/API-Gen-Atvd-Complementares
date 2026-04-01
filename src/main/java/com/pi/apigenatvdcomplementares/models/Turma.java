package com.pi.apigenatvdcomplementares.models;



import com.pi.apigenatvdcomplementares.enums.TurnoTurma;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "tb_turmas")
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "turma_id")
    private Long id;

    @Column(name = "codigo", nullable = false, unique = true, length = 50, updatable = false)
    private String codigo; // (TADS045 || 2026.1)

    @Enumerated(EnumType.STRING)
    @Column(name = "turno", nullable = false)
    private TurnoTurma turno;

    @Column(name = "semestre", length = 20)
    private String semestre;

    @Column(name = "ativa", nullable = false)
    private Boolean ativa = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    
}
