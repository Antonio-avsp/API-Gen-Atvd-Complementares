package com.pi.apigenatvdcomplementares.service;

import com.pi.apigenatvdcomplementares.dto.TurmaCreateDTO;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Turma;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.CoordenadorRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.TurmaRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository turmaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    public Turma criarTurma(TurmaCreateDTO dto, String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        validarPermissao(usuario, curso.getId());

        if (turmaRepository.existsByCodigo(dto.getCodigo())) {
            throw new RuntimeException("Já existe uma turma com esse código");
        }

        Turma turma = new Turma();
        turma.setNome(dto.getNome());
        turma.setCodigo(dto.getCodigo());
        turma.setTurno(dto.getTurno());
        turma.setSemestre(dto.getSemestre());
        turma.setCurso(curso);

        return turmaRepository.save(turma);
    }

    public List<Turma> listarTodas() {
        return turmaRepository.findAll();
    }

    public Turma buscarPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));
    }

    public List<Turma> listarPorCurso(Long cursoId) {
        return turmaRepository.findByCursoId(cursoId);
    }

    public Turma atualizarTurma(Long id, TurmaCreateDTO dto, String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        validarPermissao(usuario, curso.getId());

        if (!turma.getCodigo().equals(dto.getCodigo()) && turmaRepository.existsByCodigo(dto.getCodigo())) {
            throw new RuntimeException("Já existe uma turma com esse código");
        }

        turma.setNome(dto.getNome());
        turma.setCodigo(dto.getCodigo());
        turma.setTurno(dto.getTurno());
        turma.setSemestre(dto.getSemestre());
        turma.setCurso(curso);

        return turmaRepository.save(turma);
    }

    public void deletarTurma(Long id, String emailUsuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Turma turma = turmaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turma não encontrada"));

        validarPermissao(usuario, turma.getCurso().getId());

        turmaRepository.delete(turma);
    }

    private void validarPermissao(Usuario usuario, Long cursoId) {
        String perfil = usuario.getPerfil().name();

        if ("SUPER_ADMIN".equals(perfil)) {
            return;
        }

        if ("COORDENADOR".equals(perfil)) {
            List<CoordenadorCurso> vinculos = coordenadorRepository.findAll();

            boolean permitido = vinculos.stream()
                    .anyMatch(v -> v.getCoordenador().getId().equals(usuario.getId())
                            && v.getCurso().getId().equals(cursoId));

            if (!permitido) {
                throw new RuntimeException("Coordenador não tem permissão para este curso");
            }

            return;
        }

        throw new RuntimeException("Você não tem permissão para realizar essa ação");
    }
}