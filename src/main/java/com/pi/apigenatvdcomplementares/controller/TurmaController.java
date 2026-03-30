package com.pi.apigenatvdcomplementares.controller;

import com.pi.apigenatvdcomplementares.dto.TurmaCreateDTO;
import com.pi.apigenatvdcomplementares.models.Turma;
import com.pi.apigenatvdcomplementares.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @PostMapping
    public ResponseEntity<Turma> criarTurma(@RequestBody TurmaCreateDTO dto,
                                            Authentication authentication) {
        Turma turma = turmaService.criarTurma(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(turma);
    }

    @GetMapping
    public ResponseEntity<List<Turma>> listarTodas() {
        return ResponseEntity.ok(turmaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turma> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Turma>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(turmaService.listarPorCurso(cursoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turma> atualizarTurma(@PathVariable Long id,
                                                @RequestBody TurmaCreateDTO dto,
                                                Authentication authentication) {
        Turma turmaAtualizada = turmaService.atualizarTurma(id, dto, authentication.getName());
        return ResponseEntity.ok(turmaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id,
                                             Authentication authentication) {
        turmaService.deletarTurma(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}