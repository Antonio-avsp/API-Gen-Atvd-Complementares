package com.pi.apigenatvdcomplementares.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.CoordenadorCadastroDTO;
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
    public List<CoordenadorCurso> cadastrarCoordenadorDaTela(CoordenadorCadastroDTO dto) {

        Usuario novoCoordenador = new Usuario();
        novoCoordenador.setNome(dto.getNome());
        novoCoordenador.setEmail(dto.getEmail());

        novoCoordenador = usuarioRepository.save(novoCoordenador);

        List<CoordenadorCurso> vinculacoesSalvas = new ArrayList<>();

        for (Long idDoCurso : dto.getCursosIds()) {
            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idDoCurso));

            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(novoCoordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setEmail(novoCoordenador.getEmail());

            vinculacoesSalvas.add(coordenadorRepository.save(coordenadorCurso));
        }

        return vinculacoesSalvas;
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