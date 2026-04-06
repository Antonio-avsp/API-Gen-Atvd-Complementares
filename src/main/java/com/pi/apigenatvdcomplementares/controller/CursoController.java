package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.CursoCreateDTO;
import com.pi.apigenatvdcomplementares.dto.CursoResponseDTO;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // Endpoint para listar todos os cursos (Necessário para a tabela do Frontend)
    @GetMapping
    public ResponseEntity<List<CursoResponseDTO>> listarTodos() {
        List<Curso> cursos = cursoService.listarCursos();
        List<CursoResponseDTO> response = cursos.stream()
                .map(CursoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Endpoint para criar um novo curso
    @PostMapping
    public ResponseEntity<?> criarCurso(@Valid @RequestBody CursoCreateDTO cursoCreateDTO) {
        try {
            Curso curso = new Curso();
            curso.setNome(cursoCreateDTO.getNome());
            curso.setStatusCurso(cursoCreateDTO.isStatusCurso());
            curso.setCargaHorariaMinima(cursoCreateDTO.getCargaHorariaMinima());
            curso.setCodCurso(cursoCreateDTO.getCodCurso());

            Curso novoCurso = cursoService.salvarCurso(curso);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CursoResponseDTO(novoCurso));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Endpoint para atualizar um curso existente
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCurso(@PathVariable Long id,
            @Valid @RequestBody CursoCreateDTO cursoCreateDTO) {
        try {
            Curso curso = cursoService.editarCurso(id, cursoCreateDTO);
            return ResponseEntity.ok(new CursoResponseDTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    // Endpoint para deletar um curso
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCurso(@PathVariable Long id) {
        try {
            cursoService.deletarCurso(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("erro", "Curso não encontrado."));
        }
    }

    // Endpoint para buscar um curso específico pelo nome
    @GetMapping("/busca/{nome}")
    public ResponseEntity<CursoResponseDTO> getCursoByName(@PathVariable String nome) {
        try {
            Curso curso = cursoService.getCursoByName(nome);
            return ResponseEntity.ok(new CursoResponseDTO(curso));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}