package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> vincular(@RequestBody Map<String, Long> payload) {
        Long coordenadorId = payload.get("coordenadorId");
        Long cursoId = payload.get("cursoId");

        if (coordenadorId == null || cursoId == null) {
            return ResponseEntity.badRequest().body("IDs do coordenador e do curso são obrigatórios.");
        }

        coordenadorService.vincularCurso(coordenadorId, cursoId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Vínculo realizado com sucesso.");
    }

    @PostMapping("/cadastrar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CoordenadorCurso>> cadastrar(@RequestBody @Valid CoordenadorCadastroDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coordenadorService.cadastrarCoordenador(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    public ResponseEntity<List<CoordenadorCurso>> listarTodos() {
        return ResponseEntity.ok(coordenadorService.listarTodos());
    }

    // Rota protegida contra o TypeMismatch (deve usar /buscar/nome)
    @GetMapping("/buscar/{nome}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CoordenadorCurso>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(coordenadorService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<CoordenadorCurso>> atualizar(@PathVariable Long id,
            @RequestBody @Valid CoordenadorCadastroDTO dto) {
        return ResponseEntity.ok(coordenadorService.atualizarCoordenador(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        coordenadorService.deletarCoordenador(id);
        return ResponseEntity.noContent().build();
    }
}