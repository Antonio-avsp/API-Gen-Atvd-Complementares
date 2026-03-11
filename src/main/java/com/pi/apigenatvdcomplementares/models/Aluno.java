package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Usuario usuarioId;
    
    @Column(name = "matricula", nullable = false, unique = true, length = 20)
    private String matricula;

    @MapsId // O (ID) desta entidade será uma cópia exata do ID da entidade relacionada.
    @OneToOne(fetch = FetchType.LAZY)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Curso> cursos = new ArrayList<>();  

    @OneToMany(mappedBy = "aluno")
    private List<Submissao> submissoes = new ArrayList<>();  
}
