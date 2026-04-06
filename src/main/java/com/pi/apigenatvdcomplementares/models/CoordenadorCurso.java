package com.pi.apigenatvdcomplementares.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Email
    @Column(nullable = false, length = 100)
    private String email;

    // Usando o PerfilUsuario e garantindo que salve como TEXTO no banco
    @Enumerated(EnumType.STRING) 
    @Column(name = "nivel_acesso", nullable = false)
    private PerfilUsuario nivelAcesso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordenador_id", nullable = false)
    // Corta o loop JSON com as listas do usuário
    @JsonIgnoreProperties({ "coordenacoes", "aluno", "registrosAcao", "submissoesAvaliadas", "senha", "hibernateLazyInitializer", "handler" })
    private Usuario coordenador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    // Corta o loop JSON com as listas do curso
    @JsonIgnoreProperties({ "coordenadores", "turmas", "submissoes", "regrasAtividade", "alunoCursos", "hibernateLazyInitializer", "handler" })
    private Curso curso;
}