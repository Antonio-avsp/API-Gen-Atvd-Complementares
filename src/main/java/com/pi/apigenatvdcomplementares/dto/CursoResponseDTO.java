package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.models.Curso;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CursoResponseDTO {

    private Long id;
    private String nome;
    private String codCurso;
    private boolean statusCurso;
    private int cargaHorariaMinima;

    public CursoResponseDTO(Curso curso) {
        this.id = curso.getId();
        this.nome = curso.getNome();
        this.codCurso = curso.getCodCurso();
        this.statusCurso = curso.isStatusCurso();
        this.cargaHorariaMinima = curso.getCargaHorariaMinima();
    }
}