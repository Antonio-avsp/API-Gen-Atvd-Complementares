package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.models.Certificado;

import lombok.Getter;

@Getter
public class CertificadoDTO {

    private Long id;
    private String nomeArquivo;
    private String urlArquivo;

    public CertificadoDTO(Certificado c) {
        this.id = c.getId();
        this.nomeArquivo = c.getNomeArquivo();
        this.urlArquivo = c.getUrlArquivo();
    }

}
