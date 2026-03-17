package com.pi.apigenatvdcomplementares.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.service.AlunoService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/aluno")
@Validated
public class AlunoController {

    @Autowired
    AlunoService alunoService;

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid Aluno aluno) {
        alunoService.salvarAluno(aluno);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Aluno cadastrado com sucesso.");
    }

    @GetMapping
    public ResponseEntity<List<Aluno>> listar() {
        return ResponseEntity.ok(alunoService.listarAlunos());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Aluno> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alunoService.buscarPorId(id));
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<Aluno> buscarPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(alunoService.buscarPorMatricula(matricula));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aluno> atualizar(@PathVariable Long id, @RequestBody Aluno aluno) {
        return ResponseEntity.ok(alunoService.atualizarAluno(id, aluno));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        alunoService.deletarAluno(id);
        return ResponseEntity.noContent().build();
    }

}
