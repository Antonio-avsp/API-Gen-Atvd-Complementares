package com.pi.apigenatvdcomplementares.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurmaCreateDTO {
    private String nome;
    private String codigo;
    private String turno;
    private String semestre;
    private Long cursoId;
}