package com.pi.apigenatvdcomplementares.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.CursoCreateDTO;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cursos")
public class CursoController {

  @Autowired
  private CursoService cursoService;

  // Endpoints para criar e atualizar cursos
  @PostMapping
  public ResponseEntity<Curso> criarCurso(@Valid @RequestBody CursoCreateDTO cursoCreateDTO) {
    try {
      Curso curso = new Curso();
      curso.setNome(cursoCreateDTO.getNome());
      curso.setStatusCurso(cursoCreateDTO.isStatusCurso());
      curso.setCargaHorariaMinima(cursoCreateDTO.getCargaHorariaMinima());

      Curso novoCurso = cursoService.salvarCurso(curso);
      return ResponseEntity.status(HttpStatus.CREATED).body(novoCurso);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

      
    }
  }

  // Endpoint para atualizar um curso existente
  @PutMapping("/{id}")
  public ResponseEntity<Curso> atualizarCurso(@PathVariable Long id, @Valid @RequestBody CursoCreateDTO cursoCreateDTO) {
    try {
      Curso curso = cursoService.editarCurso(id, cursoCreateDTO);
      return ResponseEntity.ok(curso);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
  }

  // Endpoint para buscar um curso pelo nome
  @GetMapping("/{nome}")
  public ResponseEntity<Curso> getCursoByName(@PathVariable String nome) {
    try {
      Curso curso = cursoService.getCursoByName(nome);
      return ResponseEntity.ok(curso);
    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

}
