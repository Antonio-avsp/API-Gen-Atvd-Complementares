package com.pi.apigenatvdcomplementares.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.CursoCreateDTO;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.repository.CoordenadorRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;

import jakarta.transaction.Transactional;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    // Método para listar todos os cursos.
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    // Método para buscar um curso pelo nome ingorando maiúsculas e minúsculas.
    public Curso getCursoByName(String nome) {
        return cursoRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com nome: " + nome));
    }

    // Verifica se já existe um curso com o mesmo nome antes de salvar um novo curso
    public Curso salvarCurso(Curso curso) {
        if (cursoRepository.existsByNome(curso.getNome())) {
            throw new RuntimeException("Já existe um curso com este nome.");
        }
        return cursoRepository.save(curso);
    }

    // Método para deletar um curso por ID
    public void deletarCurso(Long id) {
        cursoRepository.deleteById(id);
    }

    // Método para editar um curso existente, verificando se o nome atualizado já existe em outro curso
    public Curso editarCurso(Long id, CursoCreateDTO cursoAtualizado) {
        Curso cursoExistente = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + id));

        // Verifica se o nome do curso atualizado já existe em outro curso
        if (!cursoExistente.getNome().equalsIgnoreCase(cursoAtualizado.getNome()) &&
                cursoRepository.existsByNome(cursoAtualizado.getNome())) {
            throw new RuntimeException("Já existe um curso com este nome.");
        }
       

        // Atualiza os campos do curso existente
        cursoExistente.setNome(cursoAtualizado.getNome());
        cursoExistente.setStatusCurso(cursoAtualizado.isStatusCurso());
        

        return cursoRepository.save(cursoExistente);
    }

    // Usamos o @Transactional para garantir que se a operação der errado, o banco desfaz as alterações
    @Transactional
    public Curso atualizarCoordenador(Long CursoId, Long coordenadorId) {
        Curso curso = cursoRepository.findById(CursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + CursoId));

        CoordenadorCurso coordenadorCurso = coordenadorRepository.findById(coordenadorId)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com ID: " + coordenadorId));

        curso.setCoordenadores(Set.of(coordenadorCurso));

        return cursoRepository.save(curso);
    }

}
