package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.SubmissaoRequestDTO;
import com.pi.apigenatvdcomplementares.dto.SubmissaoResponseDTO;
import com.pi.apigenatvdcomplementares.models.Submissao;
import com.pi.apigenatvdcomplementares.service.SubmissaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/submissoes")
public class SubmissaoController {

    @Autowired
    private SubmissaoService submissaoService;

    @PostMapping
    public ResponseEntity<SubmissaoResponseDTO> criar(@Valid @RequestBody SubmissaoRequestDTO dto) {
        Submissao novaSubmissao = submissaoService.criarSubmissao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SubmissaoResponseDTO(novaSubmissao));
    }

    @GetMapping
    public ResponseEntity<List<SubmissaoResponseDTO>> listarTodas() {
        List<SubmissaoResponseDTO> listaLimpa = submissaoService.listarTodas()
                .stream()
                .map(SubmissaoResponseDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaLimpa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissaoResponseDTO> buscarPorId(@PathVariable Long id) {
        Submissao submissao = submissaoService.buscarPorId(id);
        return ResponseEntity.ok(new SubmissaoResponseDTO(submissao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        submissaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<SubmissaoResponseDTO> aprovar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String feedback = (body != null) ? body.get("feedback") : null;
        Submissao submissao = submissaoService.aprovarSubmissao(id, feedback);
        return ResponseEntity.ok(new SubmissaoResponseDTO(submissao));
    }

    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<SubmissaoResponseDTO> rejeitar(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String feedback = (body != null) ? body.get("feedback") : null;
        Submissao submissao = submissaoService.rejeitarSubmissao(id, feedback);
        return ResponseEntity.ok(new SubmissaoResponseDTO(submissao));
    }
}