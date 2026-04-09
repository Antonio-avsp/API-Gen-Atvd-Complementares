package com.pi.apigenatvdcomplementares.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void vincularCurso(Long coordenadorId, Long cursoId) {
        Usuario coordenador = usuarioRepository.findById(coordenadorId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        // ✅ Regra: um coordenador não pode ser vinculado duas vezes ao mesmo curso
        if (coordenadorRepository.existsByCoordenadorIdAndCursoId(coordenadorId, cursoId)) {
            throw new RuntimeException("Este coordenador já está vinculado a este curso.");
        }

        // ✅ Regra: um curso só pode ter UM coordenador
        if (coordenadorRepository.existsByCursoId(cursoId)) {
            throw new RuntimeException(
                "O curso '" + curso.getNome() + "' já possui um coordenador vinculado. " +
                "Remova o vínculo atual antes de adicionar um novo."
            );
        }

        if (coordenador.getPerfil() != PerfilUsuario.COORDENADOR) {
            coordenador.setPerfil(PerfilUsuario.COORDENADOR);
            usuarioRepository.save(coordenador);
        }

        CoordenadorCurso vinculo = new CoordenadorCurso();
        vinculo.setCoordenador(coordenador);
        vinculo.setCurso(curso);
        vinculo.setNome(coordenador.getNome());
        vinculo.setEmail(coordenador.getEmail());
        vinculo.setNivelAcesso(PerfilUsuario.COORDENADOR);

        coordenadorRepository.save(vinculo);
    }

    @Transactional
    public List<CoordenadorCurso> cadastrarCoordenador(CoordenadorCadastroDTO dto) {
        Usuario coordenador = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        coordenador.setPerfil(PerfilUsuario.COORDENADOR);
        usuarioRepository.save(coordenador);

        List<CoordenadorCurso> vinculacoesSalvas = new ArrayList<>();

        for (Long idDoCurso : dto.getCursosIds()) {
            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado ID: " + idDoCurso));

            // ✅ Regra: coordenador já vinculado a este curso — pula sem erro
            if (coordenadorRepository.existsByCoordenadorIdAndCursoId(coordenador.getId(), idDoCurso)) {
                continue;
            }

            // ✅ Regra: curso já tem outro coordenador — lança erro informativo
            if (coordenadorRepository.existsByCursoId(idDoCurso)) {
                throw new RuntimeException(
                    "O curso '" + curso.getNome() + "' já possui um coordenador vinculado. " +
                    "Remova o vínculo atual antes de adicionar um novo."
                );
            }

            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(coordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setNome(coordenador.getNome());
            coordenadorCurso.setEmail(coordenador.getEmail());
            coordenadorCurso.setNivelAcesso(PerfilUsuario.COORDENADOR);

            vinculacoesSalvas.add(coordenadorRepository.save(coordenadorCurso));
        }

        return vinculacoesSalvas;
    }

    public List<CoordenadorCurso> listarTodos() {
        return coordenadorRepository.findAll();
    }

    public List<CoordenadorCurso> buscarPorNome(String nome) {
        List<CoordenadorCurso> coordenadores = coordenadorRepository.findByNomeContainingIgnoreCase(nome);
        if (coordenadores.isEmpty()) {
            throw new RuntimeException("Nenhum registro encontrado com o termo: " + nome);
        }
        return coordenadores;
    }

    @Transactional
    public List<CoordenadorCurso> atualizarCoordenador(Long idUsuario, CoordenadorCadastroDTO dto) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        // Remove vínculos antigos antes de recriar
        List<CoordenadorCurso> vinculosAntigos = coordenadorRepository.findAll().stream()
                .filter(v -> v.getCoordenador().getId().equals(idUsuario))
                .collect(Collectors.toList());

        coordenadorRepository.deleteAll(vinculosAntigos);

        return cadastrarCoordenador(dto);
    }

    @Transactional
    public void deletarCoordenador(Long idVinculo) {
        if (!coordenadorRepository.existsById(idVinculo)) {
            throw new RuntimeException("Vínculo não encontrado.");
        }
        coordenadorRepository.deleteById(idVinculo);
    }
}