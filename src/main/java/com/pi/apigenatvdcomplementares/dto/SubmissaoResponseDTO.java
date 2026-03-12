package com.pi.apigenatvdcomplementares.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Submissao;

import lombok.Getter;

@Getter
public class SubmissaoResponseDTO { // RETORNA A SUBMISSÃO

    private Long id;
    private LocalDateTime dataSubmissao;
    private StatusSubmissao status;
    private Set<StatusSubmissao> historicoStatus;
    private Set<CertificadoDTO> certificados;
    private String alunoNome;
    private String cursoNome;
    private String coordenadorNome;

    public SubmissaoResponseDTO(Submissao s) {
        this.id = s.getId();
        this.dataSubmissao = s.getDataSubmissao();
        this.status = s.getStatus();
        this.historicoStatus = s.getHistoricoStatus();
        this.certificados = s.getCertificados() // pega o Set<Certificado> da submissão
                .stream() // transforma o Set em um fluxo para manipulação
                .map(CertificadoDTO::new) // para cada Certificado, chama new CertificadoDTO(certificado)
                .collect(Collectors.toSet()); // junta tudo de volta em um Set, agora de CertificadoDTO
        this.alunoNome = s.getAluno().getUsuario().getNome();
        this.cursoNome = s.getCurso().getNome();
        this.coordenadorNome = s.getCoordenador() != null
                ? s.getCoordenador().getNome()
                : null;
    }
}