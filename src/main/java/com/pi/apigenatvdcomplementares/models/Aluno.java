package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_alunos")
public class Aluno extends Auditable {

    @Id
    @Column(name = "usuario_id")
    private String usuarioId;

    @Column(name = "matricula", nullable = false, unique = true, length = 20)
    private String matricula;

    // O ID desta entidade será o mesmo ID do usuário
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relação com tabela intermediária aluno_curso
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlunoCurso> cursos = new ArrayList<>();

    // Submissões feitas pelo aluno
    @OneToMany(mappedBy = "aluno")
    private List<Submissao> submissoes = new ArrayList<>();
}