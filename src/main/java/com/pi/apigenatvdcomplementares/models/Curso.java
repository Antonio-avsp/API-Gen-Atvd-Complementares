package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tb_cursos")
public class Curso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curso_id", length = 50)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "status_curso", nullable = false, length = 500)
    private boolean statusCurso;

    @Column(name = "carga_horaria_minima", nullable = false)
    private int cargaHorariaMinima;

    @OneToMany(mappedBy = "curso")
    private Set<CoordenadorCurso> coordenadores;

    @OneToMany(mappedBy = "curso")
    private List<Submissao> submissoes = new ArrayList<>();

    @OneToMany(mappedBy = "curso")
    private List<RegraAtividade> regrasAtividade = new ArrayList<>();

}
