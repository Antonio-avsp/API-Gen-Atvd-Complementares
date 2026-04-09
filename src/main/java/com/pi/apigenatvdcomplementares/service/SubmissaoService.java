package com.pi.apigenatvdcomplementares.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.SubmissaoRequestDTO;
import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Submissao;
import com.pi.apigenatvdcomplementares.repository.AlunoRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.SubmissaoRepository;

@Service
public class SubmissaoService {

    @Autowired
    private SubmissaoRepository submissaoRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private CursoRepository cursoRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    // ── Criar submissão + email de confirmação ────────────────────────────────

    public Submissao criarSubmissao(SubmissaoRequestDTO dto) {

        Submissao submissao = new Submissao();
        submissao.setTitulo(dto.getTitulo());
        submissao.setDescricao(dto.getDescricao());
        submissao.setHoras(dto.getHoras());

        Aluno aluno = new Aluno();
        aluno.setUsuarioId(dto.getAlunoId());
        submissao.setAluno(aluno);

        Curso curso = new Curso();
        curso.setId(dto.getCursoId());
        submissao.setCurso(curso);

        submissao.setStatus(StatusSubmissao.PENDENTE);
        submissao.setDataSubmissao(LocalDateTime.now());
        submissao.setHistoricoStatus(new HashSet<>());
        submissao.getHistoricoStatus().add(StatusSubmissao.PENDENTE);

        Submissao salva = submissaoRepository.save(submissao);

        // Dispara email de confirmação de forma assíncrona
        enviarEmailConfirmacao(salva);

        return salva;
    }

    private void enviarEmailConfirmacao(Submissao submissao) {
        try {
            // Busca o aluno completo com o usuário carregado
            alunoRepository.findById(submissao.getAluno().getUsuarioId()).ifPresent(aluno -> {
                if (aluno.getUsuario() != null) {
                    String emailAluno = aluno.getUsuario().getEmail();
                    String nomeAluno  = aluno.getUsuario().getNome();
                    String dataEnvio  = submissao.getDataSubmissao().format(FORMATTER);

                    String nomeCurso = submissao.getCurso() != null
                            ? submissao.getCurso().getNome()
                            : "Atividades Complementares";

                    emailService.enviarConfirmacaoSubmissao(
                            emailAluno,
                            nomeAluno,
                            nomeCurso,
                            submissao.getTitulo(),
                            submissao.getHoras(),
                            submissao.getId(),
                            dataEnvio
                    );
                }
            });
        } catch (Exception e) {
            System.err.println("Aviso: não foi possível enviar email de confirmação: " + e.getMessage());
        }
    }

    // ── Listar ───────────────────────────────────────────────────────────────

    public List<Submissao> listarTodas() {
        return submissaoRepository.findAll();
    }

    public Submissao buscarPorId(Long id) {
        return submissaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submissão não encontrada"));
    }

    public List<Submissao> listarPorAlunoMatricula(String matricula) {
        return submissaoRepository.findAllByAlunoMatricula(matricula);
    }

    public List<Submissao> listarPendentes() {
        return submissaoRepository.findByStatus(StatusSubmissao.PENDENTE);
    }

    // ── Aprovar + email ───────────────────────────────────────────────────────

    public Submissao aprovarSubmissao(Long id, String feedback) {
        if (feedback != null && !feedback.isBlank()) {
            Submissao s = buscarPorId(id);
            s.setFeedback(feedback);
            submissaoRepository.save(s);
        }
        Submissao submissao = alterarStatusSubmissao(id, StatusSubmissao.APROVADA);

        try {
            final Submissao sub = submissao;
            alunoRepository.findById(submissao.getAluno().getUsuarioId()).ifPresent(aluno -> {
                if (aluno.getUsuario() != null) {
                    String nomeCoord = sub.getCoordenador() != null
                            ? sub.getCoordenador().getNome()
                            : "Coordenador";

                    String nomeCurso = sub.getCurso() != null
                            ? sub.getCurso().getNome()
                            : "Atividades Complementares";

                    emailService.enviarAprovacao(
                            aluno.getUsuario().getEmail(),
                            aluno.getUsuario().getNome(),
                            nomeCurso,
                            sub.getTitulo(),
                            sub.getHoras(),
                            nomeCoord,
                            sub.getFeedback()
                    );
                }
            });
        } catch (Exception e) {
            System.err.println("Aviso: não foi possível enviar email de aprovação: " + e.getMessage());
        }

        return submissao;
    }

    // ── Rejeitar + email ──────────────────────────────────────────────────────

    public Submissao rejeitarSubmissao(Long id, String feedback) {
        if (feedback != null && !feedback.isBlank()) {
            Submissao s = buscarPorId(id);
            s.setFeedback(feedback);
            submissaoRepository.save(s);
        }
        Submissao submissao = alterarStatusSubmissao(id, StatusSubmissao.REPROVADA);

        try {
            final Submissao sub = submissao;
            alunoRepository.findById(submissao.getAluno().getUsuarioId()).ifPresent(aluno -> {
                if (aluno.getUsuario() != null) {
                    String nomeCoord = sub.getCoordenador() != null
                            ? sub.getCoordenador().getNome()
                            : "Coordenador";

                    String nomeCurso = sub.getCurso() != null
                            ? sub.getCurso().getNome()
                            : "Atividades Complementares";

                    emailService.enviarReprovacao(
                            aluno.getUsuario().getEmail(),
                            aluno.getUsuario().getNome(),
                            nomeCurso,
                            sub.getTitulo(),
                            nomeCoord,
                            sub.getFeedback()
                    );
                }
            });
        } catch (Exception e) {
            System.err.println("Aviso: não foi possível enviar email de reprovação: " + e.getMessage());
        }

        return submissao;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    public Submissao alterarStatusSubmissao(Long id, StatusSubmissao novoStatus) {
        Submissao submissao = buscarPorId(id);

        if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
            throw new IllegalStateException("Essa submissão já foi analisada");
        }

        submissao.setStatus(novoStatus);
        submissao.getHistoricoStatus().add(novoStatus);

        return submissaoRepository.save(submissao);
    }

    public void deletar(Long id) {
        Submissao submissaoExistente = buscarPorId(id);

        if (submissaoExistente.getStatus() != StatusSubmissao.PENDENTE) {
            throw new IllegalStateException("Não é possível excluir uma submissão já analisada");
        }

        submissaoRepository.delete(submissaoExistente);
    }
}