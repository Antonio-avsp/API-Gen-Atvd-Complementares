package com.pi.apigenatvdcomplementares.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.SubmissaoRequestDTO;
import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Submissao;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.AlunoRepository;
import com.pi.apigenatvdcomplementares.repository.CoordenadorRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.SubmissaoRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

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

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")
            .withZone(java.time.ZoneId.of("America/Sao_Paulo"));

    // ── Criar submissão + email de confirmação ────────────────────────────────

    public Submissao criarSubmissao(SubmissaoRequestDTO dto) {

        Submissao submissao = new Submissao();
        submissao.setTitulo(dto.getTitulo());
        submissao.setDescricao(dto.getDescricao());

        // Regra de negócio: carga horária máxima por submissão é 20h.
        // Se o aluno informar mais de 20h, é truncado em 20h.
        // Se for menor ou igual, mantém o valor informado.
        int horasInformadas = dto.getHoras();
        int horasFinais = Math.min(horasInformadas, 20);
        submissao.setHoras(horasFinais);

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

        enviarEmailConfirmacao(salva);

        return salva;
    }

    private void enviarEmailConfirmacao(Submissao submissao) {
        try {
            // Busca o aluno completo com o usuário carregado
            alunoRepository.findById(submissao.getAluno().getUsuarioId()).ifPresent(aluno -> {
                if (aluno.getUsuario() != null) {
                    String emailAluno = aluno.getUsuario().getEmail();
                    String nomeAluno = aluno.getUsuario().getNome();
                    String dataEnvio = submissao.getDataSubmissao()
                            .atZone(java.time.ZoneId.of("UTC"))
                            .withZoneSameInstant(java.time.ZoneId.of("America/Sao_Paulo"))
                            .format(FORMATTER);

                    // Busca o curso pelo ID para evitar problema de lazy loading
                    String nomeCurso = "Atividades Complementares";
                    if (submissao.getCurso() != null) {
                        try {
                            nomeCurso = cursoRepository.findById(submissao.getCurso().getId())
                                    .map(c -> c.getNome())
                                    .orElse("Atividades Complementares");
                        } catch (Exception ignored) {
                        }
                    }

                    emailService.enviarConfirmacaoSubmissao(
                            emailAluno,
                            nomeAluno,
                            nomeCurso,
                            submissao.getTitulo(),
                            submissao.getHoras(),
                            submissao.getId(),
                            dataEnvio);
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

                    String nomeCurso = "Atividades Complementares";
                    if (sub.getCurso() != null) {
                        try {
                            nomeCurso = cursoRepository.findById(sub.getCurso().getId())
                                    .map(c -> c.getNome())
                                    .orElse("Atividades Complementares");
                        } catch (Exception ignored) {
                        }
                    }

                    emailService.enviarAprovacao(
                            aluno.getUsuario().getEmail(),
                            aluno.getUsuario().getNome(),
                            nomeCurso,
                            sub.getTitulo(),
                            sub.getHoras(),
                            nomeCoord,
                            sub.getFeedback());
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

                    String nomeCurso = "Atividades Complementares";
                    if (sub.getCurso() != null) {
                        try {
                            nomeCurso = cursoRepository.findById(sub.getCurso().getId())
                                    .map(c -> c.getNome())
                                    .orElse("Atividades Complementares");
                        } catch (Exception ignored) {
                        }
                    }

                    emailService.enviarReprovacao(
                            aluno.getUsuario().getEmail(),
                            aluno.getUsuario().getNome(),
                            nomeCurso,
                            sub.getTitulo(),
                            nomeCoord,
                            sub.getFeedback());
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

    // ── Histórico de Submissões ──────────────────────────────────────────────

    /**
     * Lista as submissões visíveis para o usuário autenticado, aplicando a
     * regra de isolamento por perfil:
     *
     *   ALUNO        → apenas as próprias submissões
     *   COORDENADOR  → submissões dos cursos que ele coordena
     *   SUPER_ADMIN  → todas as submissões
     *
     * @param emailUsuarioLogado email retornado por Authentication#getName()
     */
    public List<Submissao> listarHistorico(String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        PerfilUsuario perfil = usuario.getPerfil();

        if (perfil == PerfilUsuario.ALUNO) {
            return submissaoRepository.findByAlunoUsuarioIdOrderByDataSubmissaoDesc(usuario.getId());
        }

        if (perfil == PerfilUsuario.COORDENADOR) {
            List<Long> cursoIds = coordenadorRepository.findByCoordenadorId(usuario.getId())
                    .stream()
                    .map(cc -> cc.getCurso() != null ? cc.getCurso().getId() : null)
                    .filter(java.util.Objects::nonNull)
                    .toList();

            if (cursoIds.isEmpty()) {
                return java.util.Collections.emptyList();
            }

            return submissaoRepository.findByCursoIdInOrderByDataSubmissaoDesc(cursoIds);
        }

        // SUPER_ADMIN
        return submissaoRepository.findAllByOrderByDataSubmissaoDesc();
    }

    /**
     * Busca uma submissão específica do histórico, validando se o usuário
     * autenticado tem permissão para visualizá-la.
     *
     * Regras:
     *   ALUNO        → só pode ver as próprias
     *   COORDENADOR  → só pode ver submissões dos cursos que coordena
     *   SUPER_ADMIN  → pode ver qualquer uma
     *
     * @throws SecurityException se o usuário não tiver permissão
     */
    public Submissao buscarHistoricoPorId(Long id, String emailUsuarioLogado) {
        Submissao submissao = buscarPorId(id);

        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado."));

        PerfilUsuario perfil = usuario.getPerfil();

        if (perfil == PerfilUsuario.SUPER_ADMIN) {
            return submissao;
        }

        if (perfil == PerfilUsuario.ALUNO) {
            boolean ehDono = submissao.getAluno() != null
                    && usuario.getId().equals(submissao.getAluno().getUsuarioId());
            if (!ehDono) {
                throw new SecurityException("Você não tem permissão para visualizar esta submissão.");
            }
            return submissao;
        }

        // COORDENADOR
        if (submissao.getCurso() == null) {
            throw new SecurityException("Submissão sem curso vinculado.");
        }
        Long cursoIdSubmissao = submissao.getCurso().getId();
        boolean coordenaCurso = coordenadorRepository.findByCoordenadorId(usuario.getId())
                .stream()
                .map(CoordenadorCurso::getCurso)
                .filter(java.util.Objects::nonNull)
                .anyMatch(c -> cursoIdSubmissao.equals(c.getId()));

        if (!coordenaCurso) {
            throw new SecurityException("Você não coordena o curso desta submissão.");
        }
        return submissao;
    }
}