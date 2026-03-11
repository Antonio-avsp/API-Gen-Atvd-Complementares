package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tb_usuarios")
public class Usuario extends Auditable {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Email(message = "Por favor, insira um e-mail válido")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private PerfilUsuario perfil;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Aluno aluno;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroAcao> registrosAcao = new ArrayList<>();

    @OneToMany(mappedBy = "coordenador")
    private List<Submissao> submissoesAvaliadas = new ArrayList<>();

    @OneToMany(mappedBy = "coordenador")
    private List<CoordenadorCurso> coordenacoes = new ArrayList<>();
}