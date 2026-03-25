package com.pi.apigenatvdcomplementares.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.pi.apigenatvdcomplementares.dto.AlunoDTO;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.service.AlunoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/alunos")
@Validated
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @PostMapping
    public ResponseEntity<AlunoDTO> criarAluno(@RequestBody @Valid AlunoDTO dto) {
        Aluno novoAluno = alunoService.salvarAluno(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AlunoDTO(novoAluno));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> listarAlunos() {
        List<AlunoDTO> alunos = alunoService.listarAlunos()
                .stream()
                .map(AlunoDTO::new)
                .toList();

        return ResponseEntity.ok(alunos);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> buscarPorId(@PathVariable Long id) {
        Aluno aluno = alunoService.buscarPorId(id);
        return ResponseEntity.ok(new AlunoDTO(aluno));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> atualizarAluno(
            @PathVariable Long id,
            @RequestBody @Valid AlunoDTO dto) {

        Aluno alunoAtualizado = alunoService.atualizarAluno(id, dto);
        return ResponseEntity.ok(new AlunoDTO(alunoAtualizado));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAluno(@PathVariable Long id) {
        alunoService.deletarAluno(id);
        return ResponseEntity.noContent().build();
    }
}