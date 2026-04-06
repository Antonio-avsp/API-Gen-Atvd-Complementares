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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.CoordenadorCadastroDTO;
import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;
import com.pi.apigenatvdcomplementares.service.CoordenadorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/coordenadores-cursos")
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

    // Sugestão: Alterar a rota de busca para evitar conflito futuro com busca por ID
    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<CoordenadorCurso>> buscarPorNome(
            @PathVariable String nome) {
        return ResponseEntity.ok(coordenadorService.buscarPorNome(nome));
    }

    // AJUSTE: Alterado de {nome} (String) para {id} (Long) para coincidir com o Service
    @PutMapping("/{id}")
    public ResponseEntity<List<CoordenadorCurso>> atualizar(@PathVariable Long id,
            @RequestBody @Valid CoordenadorCadastroDTO dto) {
        return ResponseEntity.ok(coordenadorService.atualizarCoordenador(id, dto));
    }

    // AJUSTE: Alterado para Long id e retorno ResponseEntity<Void> (pois o service agora é void)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        coordenadorService.deletarCoordenador(id);
        return ResponseEntity.noContent().build();
    }
}