package com.pi.apigenatvdcomplementares.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.AlunoRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

@Service
public class AlunoService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    public Aluno SalvarAluno(Aluno aluno) {
        if (alunoRepository.existsByMatricula(aluno.getMatricula())) {
            throw new RuntimeException("Já existe um aluno com esta matrícula."); // Verifica se já existe um aluno com
                                                                                  // a mesma matrícula antes de salvar
        }

        if (aluno.getUsuario() == null || aluno.getUsuario().getId() == null) { // Verifica se o aluno tem um usuário associado e se o ID do usuário é válido
            throw new RuntimeException("O usuário associado ao aluno é inválido.");
        }
        Usuario usuario = usuarioRepository.findById(aluno.getUsuario().getId())  
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!!"));

        if (alunoRepository.existsByUsuarioId(usuario.getId())) { // Verifica se já existe um aluno associado a este usuário
            throw new RuntimeException("Já existe um aluno associado a este usuário.");
        }

        aluno.setUsuario(usuario);

        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarAlunos() { // Método para listar todos os alunos
        return alunoRepository.findAll();
    }

    public Aluno buscarPorId(String id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com id: " + id)); // Método para buscar aluno por ID
    }  
    
    public Aluno buscarPorMatricula(String matricula) {
        return alunoRepository.findByMatricula(matricula)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com matrícula: " + matricula)); // Método para buscar aluno por matrícula
    }

    public Aluno atualizarAluno(String id, Aluno alunoAtualizado) { // Método para atualizar um aluno existente
        Aluno alunoExistente = buscarPorId(id);

        if (!alunoExistente.getMatricula().equals(alunoAtualizado.getMatricula())
                && alunoRepository.existsByMatricula(alunoAtualizado.getMatricula())) {
            throw new RuntimeException("Já existe um aluno com essa matrícula.");
        }

        alunoExistente.setMatricula(alunoAtualizado.getMatricula());

        return alunoRepository.save(alunoExistente);
    }

    public void deletarAluno(String id) { // Método para deletar um aluno por ID
        Aluno alunoExistente = buscarPorId(id);
        alunoRepository.delete(alunoExistente);
    }
}
