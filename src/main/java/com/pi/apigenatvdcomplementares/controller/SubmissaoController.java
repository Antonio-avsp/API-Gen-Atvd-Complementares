package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.HistoricoSubmissaoDTO;
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

    // ── Histórico de Submissões ──────────────────────────────────────────────

    /**
     * Lista o histórico de submissões filtrado pelo perfil do usuário logado:
     *   - ALUNO        → próprias submissões
     *   - COORDENADOR  → submissões dos cursos que coordena
     *   - SUPER_ADMIN  → todas
     *
     * O filtro é aplicado no serviço com base no email extraído do JWT
     * (Authentication#getName()).
     */
    @GetMapping("/historico")
    public ResponseEntity<List<HistoricoSubmissaoDTO>> listarHistorico(Authentication authentication) {
        String email = authentication.getName();
        List<HistoricoSubmissaoDTO> historico = submissaoService.listarHistorico(email)
                .stream()
                .map(HistoricoSubmissaoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(historico);
    }

    /**
     * Detalhe de uma submissão específica do histórico, com checagem de
     * autorização: aluno só vê as próprias, coordenador só dos cursos que
     * coordena, super_admin vê qualquer uma. Em caso de acesso negado,
     * retorna 403 (mapeado via SecurityException no handler global).
     */
    @GetMapping("/historico/{id}")
    public ResponseEntity<?> buscarHistoricoPorId(
            @PathVariable Long id,
            Authentication authentication) {
        String email = authentication.getName();
        try {
            Submissao submissao = submissaoService.buscarHistoricoPorId(id, email);
            return ResponseEntity.ok(new HistoricoSubmissaoDTO(submissao));
        } catch (SecurityException e) {
            // 403 Forbidden quando o usuário não tem permissão para ver esta submissão
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("erro", e.getMessage()));
        }
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