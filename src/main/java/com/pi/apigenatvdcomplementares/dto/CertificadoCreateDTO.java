package com.pi.apigenatvdcomplementares.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificadoCreateDTO {

    @NotBlank(message = "O nome do arquivo é obrigatório")
    private String nomeArquivo;

    @NotBlank(message = "A URL do arquivo é obrigatória")
    private String urlArquivo;

    // Precisamos do ID da submissão para saber de qual atividade é este certificado
    @NotNull(message = "O ID da submissão é obrigatório")
    private Long submissaoId; 
}