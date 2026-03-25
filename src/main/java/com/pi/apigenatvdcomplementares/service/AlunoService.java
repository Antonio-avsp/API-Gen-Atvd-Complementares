package com.pi.apigenatvdcomplementares.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.AlunoDTO;
import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.AlunoCurso;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.AlunoRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Transactional
    public Aluno salvarAluno(AlunoDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário com esse email não encontrado."));

        if (usuario.getPerfil() != PerfilUsuario.ALUNO) {
            throw new RuntimeException("O usuário informado não possui perfil de aluno.");
        }

        if (alunoRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Já existe um aluno vinculado a esse usuário.");
        }

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        Aluno aluno = new Aluno();
        aluno.setUsuario(usuario);
        aluno.setMatricula(dto.getMatricula());

        Aluno alunoSalvo = alunoRepository.save(aluno);

        AlunoCurso alunoCurso = new AlunoCurso();
        alunoCurso.setAluno(alunoSalvo);
        alunoCurso.setCurso(curso);

        alunoSalvo.getCursos().add(alunoCurso);

        return alunoRepository.save(alunoSalvo);
    }

    public List<Aluno> listarAlunos() {
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
    }

    @Transactional
    public Aluno atualizarAluno(Long id, AlunoDTO dto) {
        Aluno alunoExistente = buscarPorId(id);

        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário com esse email não encontrado."));

        if (usuario.getPerfil() != PerfilUsuario.ALUNO) {
            throw new RuntimeException("O usuário informado não possui perfil de aluno.");
        }

        Curso curso = cursoRepository.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        alunoExistente.setUsuario(usuario);
        alunoExistente.setMatricula(dto.getMatricula());

        alunoExistente.getCursos().clear();

        AlunoCurso alunoCurso = new AlunoCurso();
        alunoCurso.setAluno(alunoExistente);
        alunoCurso.setCurso(curso);

        alunoExistente.getCursos().add(alunoCurso);

        return alunoRepository.save(alunoExistente);
    }

    public void deletarAluno(Long id) {
        Aluno aluno = buscarPorId(id);
        alunoRepository.delete(aluno);
    }
}