package com.pi.apigenatvdcomplementares.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Submissao;

import lombok.Getter;

@Getter
public class SubmissaoResponseDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private Integer horas;
    private String feedback;
    private LocalDateTime dataSubmissao;
    private StatusSubmissao status;
    private Set<StatusSubmissao> historicoStatus;
    private Set<CertificadoDTO> certificados;
    private String alunoNome;
    private String cursoNome;
    private String coordenadorNome;
    private String turmaNome; // Novo campo adicionado para integração

    public SubmissaoResponseDTO(Submissao s) {
        this.id = s.getId();
        this.titulo = s.getTitulo();
        this.descricao = s.getDescricao();
        this.horas = s.getHoras();
        this.feedback = s.getFeedback();
        this.dataSubmissao = s.getDataSubmissao();
        this.status = s.getStatus();
        this.historicoStatus = s.getHistoricoStatus();

        // 1. Proteção contra Certificados nulos
        this.certificados = s.getCertificados() != null 
                ? s.getCertificados().stream()
                    .map(CertificadoDTO::new)
                    .collect(Collectors.toSet())
                : Collections.emptySet();

        // 2. Proteção para o Nome do Aluno
        this.alunoNome = (s.getAluno() != null && s.getAluno().getUsuario() != null)
                ? s.getAluno().getUsuario().getNome()
                : "Nome não disponível";

        // 3. Proteção para o Nome do Curso
        this.cursoNome = (s.getCurso() != null) 
                ? s.getCurso().getNome() 
                : "Curso não informado";

        // 4. Proteção para o Coordenador
        this.coordenadorNome = (s.getCoordenador() != null)
                ? s.getCoordenador().getNome()
                : "Aguardando atribuição";

        // 5. Proteção para o Nome da Turma (Integração Aluno -> Turma)
        this.turmaNome = (s.getAluno() != null && s.getAluno().getTurma() != null)
                ? s.getAluno().getTurma().getNome()
                : "Sem Turma vinculada";
    }
}