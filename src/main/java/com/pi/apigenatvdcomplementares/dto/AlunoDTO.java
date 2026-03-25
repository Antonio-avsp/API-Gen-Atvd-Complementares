package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.models.Aluno;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlunoDTO {

    private String email;
    private String matricula;
    private Long cursoId;
    private Long usuarioId;

    public AlunoDTO(Aluno aluno) {
        this.usuarioId = aluno.getUsuarioId();
        this.matricula = aluno.getMatricula();
        this.email = aluno.getUsuario() != null ? aluno.getUsuario().getEmail() : null;

        if (aluno.getCursos() != null && !aluno.getCursos().isEmpty()) {
            this.cursoId = aluno.getCursos().get(0).getCurso().getId();
        }
    }
}