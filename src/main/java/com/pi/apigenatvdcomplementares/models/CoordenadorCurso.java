package com.pi.apigenatvdcomplementares.models;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "coordenador_curso")
public class CoordenadorCurso extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Gera automaticamente um id do tipo:
                                                    // "550e8400-e29b-41d4-a716-446655440000"
    private String id;

    @ManyToOne(fetch = FetchType.LAZY) // MUITOS CoordenadorCurso → UM Usuario (PERMITE QUE UM USUÁRIO (COORDENADOR),
                                       // COORDENE MAIS DE UM CURSO)
    @JoinColumn(name = "coordenador_id", nullable = false)
    private Usuario coordenador;

    @OneToOne(fetch = FetchType.LAZY) // Cada curso tem apenas um coordenador.
    @JoinColumn(name = "curso_id", nullable = false, unique = true) // Unique → um curso só pode aparecer uma vez
    private Curso curso;

}
