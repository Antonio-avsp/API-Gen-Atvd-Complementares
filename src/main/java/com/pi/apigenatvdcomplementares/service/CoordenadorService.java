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

    // Método para cadastrar um coordenador em um curso
    @Transactional
    public List<CoordenadorCurso> cadastrarCoordenadorDaTela(CoordenadorCadastroDTO dto) {

        // 1. Verificar se o e-mail já existe (opcional, mas recomendado) ou criar o
        // novo Usuário
        Usuario novoCoordenador = new Usuario();
        novoCoordenador.setNome(dto.getNome());
        novoCoordenador.setEmail(dto.getEmail());

        // Salva o usuário primeiro para gerar um ID para ele
        novoCoordenador = usuarioRepository.save(novoCoordenador);

        // Lista para armazenar as associações que serão salvas
        List<CoordenadorCurso> vinculacoesSalvas = new ArrayList<>();

        // 2. Faz um loop para cada curso selecionado lá no Front-end ("um ou mais")
        for (Long idDoCurso : dto.getCursosIds()) {

            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idDoCurso));

            // Cria a tabela associativa
            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(novoCoordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setEmail(novoCoordenador.getEmail());

            // Salva a associação no banco e adiciona na lista de retorno
            vinculacoesSalvas.add(coordenadorRepository.save(coordenadorCurso));
        }

        return vinculacoesSalvas;
    }

    // Método para deletar um coordenador pelo nome
    public CoordenadorCurso deletarCoordenador(String nome) {
        CoordenadorCurso coordenadorCurso = coordenadorRepository.findByName(nome)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com nome: " + nome));

        coordenadorRepository.delete(coordenadorCurso);
        return coordenadorCurso;
    }
}
