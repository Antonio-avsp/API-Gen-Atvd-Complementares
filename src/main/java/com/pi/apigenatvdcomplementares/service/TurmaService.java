package com.pi.apigenatvdcomplementares.service;

import com.pi.apigenatvdcomplementares.dto.TurmaCreateDTO;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Turma;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.CoordenadorRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.TurmaRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

    @Transactional
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
        turma.setCodigo(dto.getCodigo());
        
        // AJUSTE: Usando o código como nome para satisfazer a restrição do banco
        turma.setNome(dto.getCodigo()); 
        
        turma.setTurno(dto.getTurno());
        turma.setSemestre(dto.getSemestre());
        turma.setCurso(curso);
        turma.setAtiva(true); // Garante que a turma nasça ativa

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

    @Transactional
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

        turma.setCodigo(dto.getCodigo());
        
        // AJUSTE: Atualiza o nome também caso o código mude
        turma.setNome(dto.getCodigo()); 
        
        turma.setTurno(dto.getTurno());
        turma.setSemestre(dto.getSemestre());
        turma.setCurso(curso);

        return turmaRepository.save(turma);
    }

    @Transactional
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
            // Utilizando o método otimizado do repositório que adicionamos anteriormente
            boolean permitido = coordenadorRepository.existsByCoordenadorIdAndCursoId(usuario.getId(), cursoId);

            if (!permitido) {
                throw new RuntimeException("Coordenador não tem permissão para este curso");
            }

            return;
        }

        throw new RuntimeException("Você não tem permissão para realizar essa ação");
    }
}