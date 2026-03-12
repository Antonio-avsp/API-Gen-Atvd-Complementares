package com.pi.apigenatvdcomplementares.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubmissaoRequestDTO { // CRIA SUBMISSÃO

    @NotNull
    private Long alunoId;

    @NotNull
    private Long cursoId;
}
