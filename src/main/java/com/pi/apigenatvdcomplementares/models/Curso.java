package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cursos")
@Getter
@Setter
public class Curso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "curso_id", length = 50)
    private Long id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "carga_horaria_minima", nullable = false)
    private int cargaHorariaMinima;

    @OneToMany(mappedBy = "curso")
    private List<CoordenadorCurso> coordenadores = new ArrayList<>();

    @OneToMany(mappedBy = "curso")
    private List<Submissao> submissoes = new ArrayList<>();

    @OneToMany(mappedBy = "curso")
    private List<RegraAtividade> regrasAtividade = new ArrayList<>();

}
