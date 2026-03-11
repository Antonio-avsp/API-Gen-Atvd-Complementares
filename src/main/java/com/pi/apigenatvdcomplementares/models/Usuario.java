package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_usuarios")
public class Usuario extends Auditable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @Email(message = "Por favor, insira um e-mail válido")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER) // indica que é uma coleção de elementos simples (enum) e deve ser carregada imediatamente
    @CollectionTable(name = "tb_perfis", joinColumns = @JoinColumn(name = "usuario_id")) // Tabela de associação dos perfis de usuario
    @Enumerated(EnumType.STRING) // Armazena o enum como string no banco de dados
    @Column(name = "perfis")
    private Set<PerfilUsuario> perfis;

    // @Embedded // Indica que os campos de Aluno são parte da entidade Usuario e serão mapeados para as colunas da tabela de Usuario.
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Aluno aluno;

    // Um usuário pode ter vários registros de ação.
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegistroAcao> registrosAcao = new ArrayList<>();

    // Um coordenador pode avaliar várias submissões.
    @OneToMany(mappedBy = "coordenadorAvaliador")
    private List<Submissao> submissoesAvaliadas = new ArrayList<>();

    // Um coordenador pode estar associado a vários cursos.
    @OneToMany(mappedBy = "coordenador")
    private List<CoordenadorCurso> coordenacoes = new ArrayList<>();

}
