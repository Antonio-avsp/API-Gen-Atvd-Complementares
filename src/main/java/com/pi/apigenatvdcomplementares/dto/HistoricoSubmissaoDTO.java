package com.pi.apigenatvdcomplementares.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Submissao;

import lombok.Getter;

/**
 * DTO de Histórico de Submissões.
 *
 * Usado pelos endpoints:
 *   - GET /submissoes/historico
 *   - GET /submissoes/historico/{id}
 *
 * Diferente do {@link SubmissaoResponseDTO} (que expõe os campos brutos),
 * este DTO entrega os nomes que o front (web e mobile) espera consumir
 * diretamente na tela de Histórico de Submissões:
 *
 *   identificacao       → "Curso XYZ — Submissão #123"
 *   tipo                → "Atividade Complementar" (fixo por enquanto)
 *   dataSubmissao       → ISO-8601
 *   alunoId / alunoNome → quem enviou
 *   cursoNome           → curso ao qual pertence
 *   status              → status atual
 *   historicoStatus     → linha do tempo de mudanças
 *   quantidadeRegistros → tamanho da lista de certificados anexados
 *   observacao          → feedback do coordenador (mesmo campo do back)
 *   certificados        → lista resumida (id, nome, url)
 */
@Getter
public class HistoricoSubmissaoDTO {

    private Long id;
    private String identificacao;
    private String tipo;
    private LocalDateTime dataSubmissao;
    private Long alunoId;
    private String alunoNome;
    private String cursoNome;
    private StatusSubmissao status;
    private Set<StatusSubmissao> historicoStatus;
    private Integer quantidadeRegistros;
    private String observacao;
    private List<CertificadoDTO> certificados;

    public HistoricoSubmissaoDTO(Submissao s) {
        this.id = s.getId();

        // "Curso — Submissão #id" — útil como título no card da tela
        String nomeCurso = (s.getCurso() != null) ? s.getCurso().getNome() : "Curso não informado";
        this.identificacao = nomeCurso + " — Submissão #" + s.getId();

        // Tipo fixo (futuro: pode virar enum se houver outros tipos de submissão)
        this.tipo = "Atividade Complementar";

        this.dataSubmissao = s.getDataSubmissao();

        // Dados do aluno (defensivos contra nulos)
        this.alunoId = (s.getAluno() != null) ? s.getAluno().getUsuarioId() : null;
        this.alunoNome = (s.getAluno() != null && s.getAluno().getUsuario() != null)
                ? s.getAluno().getUsuario().getNome()
                : "Nome não disponível";

        this.cursoNome = nomeCurso;

        this.status = s.getStatus();
        this.historicoStatus = s.getHistoricoStatus();

        // O feedback do coordenador é exibido como "observação" na tela
        this.observacao = s.getFeedback();

        // Certificados convertidos para a representação resumida
        List<CertificadoDTO> certs = (s.getCertificados() != null)
                ? s.getCertificados().stream().map(CertificadoDTO::new).collect(Collectors.toList())
                : Collections.emptyList();
        this.certificados = certs;
        this.quantidadeRegistros = certs.size();
    }
}
