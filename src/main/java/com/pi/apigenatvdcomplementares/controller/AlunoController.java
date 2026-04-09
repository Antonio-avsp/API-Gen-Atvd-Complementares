package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.pi.apigenatvdcomplementares.dto.AlunoDTO;
import com.pi.apigenatvdcomplementares.dto.CursoResponseDTO;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;
import com.pi.apigenatvdcomplementares.service.AlunoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Retorna os dados do aluno logado.
     * GET /alunos/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        try {
            Aluno aluno = alunoService.buscarPorId(usuario.getId());
            return ResponseEntity.ok(new AlunoDTO(aluno));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não cadastrado.");
        }
    }

    /**
     * Retorna apenas os cursos nos quais o aluno logado está matriculado.
     * GET /alunos/me/cursos
     */
    @GetMapping("/me/cursos")
    public ResponseEntity<?> getMeusCursos(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        try {
            Aluno aluno = alunoService.buscarPorId(usuario.getId());

            List<CursoResponseDTO> cursos = aluno.getCursos().stream()
                    .map(alunoCurso -> new CursoResponseDTO(alunoCurso.getCurso()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(cursos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno não cadastrado.");
        }
    }

    @PostMapping
    public ResponseEntity<AlunoDTO> criarAluno(@Valid @RequestBody AlunoDTO dto) {
        Aluno novoAluno = alunoService.salvarAluno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AlunoDTO(novoAluno));
    }

    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarAlunos() {
        List<AlunoDTO> alunos = alunoService.listarAlunos()
                .stream()
                .map(AlunoDTO::new)
                .toList();
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(new AlunoDTO(aluno));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizarAluno(
            @PathVariable Long id,
            @Valid @RequestBody AlunoDTO dto) {
        Aluno alunoAtualizado = alunoService.atualizarAluno(id, dto);
        return ResponseEntity.ok(new AlunoDTO(alunoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletarAluno(id);
        return ResponseEntity.noContent().build();
    }
}