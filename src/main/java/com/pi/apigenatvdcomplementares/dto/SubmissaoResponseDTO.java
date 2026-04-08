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
    private String turmaNome;

    /** ID do aluno (tb_alunos.usuario_id) — usado pelo front para filtrar submissões do aluno logado */
    private Long alunoId;

    /** ID do curso (tb_cursos.curso_id) — usado pelo front para filtrar por curso selecionado */
    private Long cursoId;

    public SubmissaoResponseDTO(Submissao s) {
        this.id = s.getId();
        this.titulo = s.getTitulo();
        this.descricao = s.getDescricao();
        this.horas = s.getHoras();
        this.feedback = s.getFeedback();
        this.dataSubmissao = s.getDataSubmissao();
        this.status = s.getStatus();
        this.historicoStatus = s.getHistoricoStatus();

        // Proteção contra certificados nulos
        this.certificados = s.getCertificados() != null
                ? s.getCertificados().stream()
                    .map(CertificadoDTO::new)
                    .collect(Collectors.toSet())
                : Collections.emptySet();

        // Nome do aluno
        this.alunoNome = (s.getAluno() != null && s.getAluno().getUsuario() != null)
                ? s.getAluno().getUsuario().getNome()
                : "Nome não disponível";

        // ✅ ID do aluno (campo novo) — necessário para o front filtrar por aluno logado
        this.alunoId = (s.getAluno() != null)
                ? s.getAluno().getUsuarioId()
                : null;

        // Nome do curso
        this.cursoNome = (s.getCurso() != null)
                ? s.getCurso().getNome()
                : "Curso não informado";

        // ✅ ID do curso (campo novo) — necessário para o front filtrar por curso selecionado
        this.cursoId = (s.getCurso() != null)
                ? s.getCurso().getId()
                : null;

        // Coordenador
        this.coordenadorNome = (s.getCoordenador() != null)
                ? s.getCoordenador().getNome()
                : "Aguardando atribuição";

        // Turma do aluno
        this.turmaNome = (s.getAluno() != null && s.getAluno().getTurma() != null)
                ? s.getAluno().getTurma().getNome()
                : "Sem Turma vinculada";
    }
}