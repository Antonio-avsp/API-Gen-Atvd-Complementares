package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.enums.TurnoTurma;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TurmaCreateDTO {
    private String codigo;
    private TurnoTurma turno;
    private String semestre;
    private Long cursoId;
}