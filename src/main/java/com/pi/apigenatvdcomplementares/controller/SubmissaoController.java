package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
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

    /**
     * Aluno cria uma nova submissão de atividade complementar.
     */
    @PostMapping
    public ResponseEntity<SubmissaoResponseDTO> criar(@Valid @RequestBody SubmissaoRequestDTO dto) {
        Submissao novaSubmissao = submissaoService.criarSubmissao(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new SubmissaoResponseDTO(novaSubmissao));
    }

    /**
     * Lista todas as submissões — aluno, coordenador e admin podem visualizar.
     */
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

    /**
     * Apenas aluno pode deletar a própria submissão (somente PENDENTE).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        submissaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Apenas coordenador e admin podem aprovar/rejeitar.
     */
    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<SubmissaoResponseDTO> aprovar(@PathVariable Long id) {
        Submissao submissao = submissaoService.aprovarSubmissao(id);
        return ResponseEntity.ok(new SubmissaoResponseDTO(submissao));
    }

    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<SubmissaoResponseDTO> rejeitar(@PathVariable Long id) {
        Submissao submissao = submissaoService.rejeitarSubmissao(id);
        return ResponseEntity.ok(new SubmissaoResponseDTO(submissao));
    }
}