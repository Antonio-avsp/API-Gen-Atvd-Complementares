package com.pi.apigenatvdcomplementares.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.CoordenadorCadastroDTO;
import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.CoordenadorRepository;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class CoordenadorService {

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public List<CoordenadorCurso> cadastrarCoordenador(CoordenadorCadastroDTO dto) {

        Usuario coordenador = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário com esse email não foi encontrado."));

        List<CoordenadorCurso> vinculacoesSalvas = new ArrayList<>();

        for (Long idDoCurso : dto.getCursosIds()) {
            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idDoCurso));

            boolean jaVinculado = coordenador.getCoordenacoes().stream()
                    .anyMatch(v -> v.getCurso().getId().equals(idDoCurso));

            if (jaVinculado) {
                continue;
            }

            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(coordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setEmail(coordenador.getEmail());

            vinculacoesSalvas.add(coordenadorRepository.save(coordenadorCurso));
        }

        return vinculacoesSalvas;
    }

    public List<CoordenadorCurso> listarTodos() {
        return coordenadorRepository.findAll();
    }

    public List<CoordenadorCurso> buscarPorNome(String nome) {
        List<CoordenadorCurso> coordenadores = coordenadorRepository.findByCoordenadorNome(nome);

        if (coordenadores.isEmpty()) {
            throw new RuntimeException("Coordenador não encontrado com nome: " + nome);
        }

        return coordenadores;
    }

    @Transactional
    public List<CoordenadorCurso> atualizarCoordenador(String nome, CoordenadorCadastroDTO dto) {
        List<CoordenadorCurso> coordenadoresExistentes = coordenadorRepository.findByCoordenadorNome(nome);

        if (coordenadoresExistentes.isEmpty()) {
            throw new RuntimeException("Coordenador não encontrado com nome: " + nome);
        }

        Usuario coordenador = coordenadoresExistentes.get(0).getCoordenador();
        coordenador.setNome(dto.getNome());
        coordenador.setEmail(dto.getEmail());
        coordenador.setPerfil(PerfilUsuario.COORDENADOR);

        usuarioRepository.save(coordenador);

        coordenadorRepository.deleteAll(coordenadoresExistentes);

        List<CoordenadorCurso> novasVinculacoes = new ArrayList<>();

        for (Long idDoCurso : dto.getCursosIds()) {
            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idDoCurso));

            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(coordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setEmail(coordenador.getEmail());

            novasVinculacoes.add(coordenadorRepository.save(coordenadorCurso));
        }

        return novasVinculacoes;
    }

    @Transactional
    public List<CoordenadorCurso> deletarCoordenador(String nome) {
        List<CoordenadorCurso> coordenadores = coordenadorRepository.findByCoordenadorNome(nome);

        if (coordenadores.isEmpty()) {
            throw new RuntimeException("Coordenador não encontrado com nome: " + nome);
        }

        coordenadorRepository.deleteAll(coordenadores);
        return coordenadores;
    }
}