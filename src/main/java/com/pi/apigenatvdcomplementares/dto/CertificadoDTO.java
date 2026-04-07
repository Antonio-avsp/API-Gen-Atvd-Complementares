package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.models.Certificado;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CertificadoDTO {

    private Long id;
    private String nomeArquivo;
    private String urlArquivo;

    // Construtor limpo, pegando apenas o que pertence ao Certificado
    public CertificadoDTO(Certificado c) {
        this.id = c.getId();
        this.nomeArquivo = c.getNomeArquivo();
        this.urlArquivo = c.getUrlArquivo();
    }
}