package com.pi.apigenatvdcomplementares.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.pi.apigenatvdcomplementares.dto.CoordenadorCadastroDTO;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.service.CoordenadorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/coordenadores")
@Validated
public class CoordenadorController {

    @Autowired
    private CoordenadorService coordenadorService;

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody @Valid CoordenadorCadastroDTO dto) {
        coordenadorService.cadastrarCoordenador(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Coordenador cadastrado com sucesso.");
    }

    @GetMapping
    public ResponseEntity<List<CoordenadorCurso>> listarTodos() {
        return ResponseEntity.ok(coordenadorService.listarTodos());
    }

    @GetMapping("/{nome}")
    public ResponseEntity<List<CoordenadorCurso>> buscarPorNome(
            @PathVariable String nome) {

        return ResponseEntity.ok(coordenadorService.buscarPorNome(nome));
    }

    @PutMapping("/{nome}")
    public ResponseEntity<List<CoordenadorCurso>> atualizar(
            @PathVariable String nome,
            @RequestBody @Valid CoordenadorCadastroDTO dto) {

        return ResponseEntity.ok(coordenadorService.atualizarCoordenador(nome, dto));
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<List<CoordenadorCurso>> deletar(
            @PathVariable String nome) {

        return ResponseEntity.ok(coordenadorService.deletarCoordenador(nome));
    }
}