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
    public List<CoordenadorCurso> cadastrarCoordenador(CoordenadorCadastroDTO dto) {
        // Busca o usuário pelo e-mail enviado no DTO
        Usuario coordenador = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário com esse e-mail não foi encontrado."));

        // Garante que o usuário tenha o perfil de Coordenador
        coordenador.setPerfil(PerfilUsuario.COORDENADOR);
        usuarioRepository.save(coordenador);

        List<CoordenadorCurso> vinculacoesSalvas = new ArrayList<>();

        for (Long idDoCurso : dto.getCursosIds()) {
            Curso curso = cursoRepository.findById(idDoCurso)
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado com ID: " + idDoCurso));

            // Verifica se já existe o vínculo específico para este curso
            boolean jaVinculado = coordenadorRepository.existsByCoordenadorIdAndCursoId(coordenador.getId(), idDoCurso);

            if (jaVinculado) {
                continue;
            }

            CoordenadorCurso coordenadorCurso = new CoordenadorCurso();
            coordenadorCurso.setCoordenador(coordenador);
            coordenadorCurso.setCurso(curso);
            coordenadorCurso.setNome(coordenador.getNome()); // Preenche o nome da entidade CoordenadorCurso
            coordenadorCurso.setEmail(coordenador.getEmail());

            vinculacoesSalvas.add(coordenadorRepository.save(coordenadorCurso));
        }

        return vinculacoesSalvas;
    }

    public List<CoordenadorCurso> listarTodos() {
        return coordenadorRepository.findAll();
    }

    // Atualizado para usar o novo método do repositório
    public List<CoordenadorCurso> buscarPorNome(String nome) {
        List<CoordenadorCurso> coordenadores = coordenadorRepository.findByNomeContainingIgnoreCase(nome);

        if (coordenadores.isEmpty()) {
            throw new RuntimeException("Nenhum registro de coordenador encontrado com o termo: " + nome);
        }

        return coordenadores;
    }

    @Transactional
    public List<CoordenadorCurso> atualizarCoordenador(Long idUsuario, CoordenadorCadastroDTO dto) {
        // 1. Busca o usuário que terá seus vínculos atualizados
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID: " + idUsuario));

        // 2. Atualiza os dados básicos do usuário
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuarioRepository.save(usuario);

        // 3. Remove todos os vínculos atuais deste coordenador para recriá-los (estratégia comum de atualização)
        // Nota: Você pode precisar adicionar List<CoordenadorCurso> findByCoordenadorId(Long id) ao seu repo
        List<CoordenadorCurso> vinculosAntigos = coordenadorRepository.findAll().stream()
                .filter(v -> v.getCoordenador().getId().equals(idUsuario))
                .collect(Collectors.toList());
        
        coordenadorRepository.deleteAll(vinculosAntigos);

        // 4. Cria os novos vínculos baseados nos cursosIds do DTO
        return cadastrarCoordenador(dto);
    }

    @Transactional
    public void deletarCoordenador(Long idUsuario) {
        // Encontra todos os vínculos (tb_coordenadores_curso) ligados a este usuário ID
        List<CoordenadorCurso> vinculos = coordenadorRepository.findAll().stream()
                .filter(v -> v.getCoordenador().getId().equals(idUsuario))
                .collect(Collectors.toList());

        if (vinculos.isEmpty()) {
            throw new RuntimeException("Vínculos não encontrados para o usuário com ID: " + idUsuario);
        }

        coordenadorRepository.deleteAll(vinculos);
    }
}