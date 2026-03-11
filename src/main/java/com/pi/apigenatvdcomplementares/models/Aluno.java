package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "alunos")
public class Aluno extends Auditable {
    
    @Id 
    @Column(name = "usuario_id", length = 36)
    private String usuarioId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // Indica que o ID do Aluno é o mesmo do Usuario associado.
    private Usuario usuario;
    
    @Column(name = "matricula", nullable = false, unique = true, length = 20)
    private String matricula;

    @OneToMany (mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Curso> cursos = new ArrayList<>();  

    @OneToMany(mappedBy = "aluno")
    private List<Submissao> submissoes = new ArrayList<>();  
}
