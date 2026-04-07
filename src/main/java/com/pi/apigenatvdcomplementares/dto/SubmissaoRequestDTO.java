package com.pi.apigenatvdcomplementares.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SubmissaoRequestDTO { // CRIA SUBMISSÃO

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull(message = "A carga horária é obrigatória")
    @Min(value = 1, message = "A atividade deve ter no mínimo 1 hora")
    private Integer horas;

    @NotNull
    private Long alunoId;

    @NotNull
    private Long cursoId;
}