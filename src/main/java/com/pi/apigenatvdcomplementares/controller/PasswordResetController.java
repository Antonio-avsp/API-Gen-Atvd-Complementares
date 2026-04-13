package com.pi.apigenatvdcomplementares.controller;

import com.pi.apigenatvdcomplementares.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * POST /auth/password/solicitar
     * Body: { "email": "usuario@email.com" }
     */
    @PostMapping("/solicitar")
    public ResponseEntity<Map<String, String>> solicitar(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Email é obrigatório."));
        }
        passwordResetService.solicitarRecuperacao(email);
        // Sempre retorna sucesso (não revela se email existe)
        return ResponseEntity.ok(Map.of("mensagem", "Se o e-mail estiver cadastrado, o código foi enviado."));
    }

    /**
     * POST /auth/password/validar
     * Body: { "email": "...", "codigo": "123456" }
     */
    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> validar(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");

        boolean valido = passwordResetService.validarCodigo(email, codigo);
        if (!valido) {
            return ResponseEntity.badRequest().body(Map.of("valido", false, "erro", "Código inválido ou expirado."));
        }
        return ResponseEntity.ok(Map.of("valido", true));
    }

    /**
     * POST /auth/password/redefinir
     * Body: { "email": "...", "codigo": "123456", "novaSenha": "..." }
     */
    @PostMapping("/redefinir")
    public ResponseEntity<Map<String, String>> redefinir(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String codigo = body.get("codigo");
        String novaSenha = body.get("novaSenha");

        if (novaSenha == null || novaSenha.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("erro", "A senha deve ter pelo menos 6 caracteres."));
        }

        try {
            passwordResetService.redefinirSenha(email, codigo, novaSenha);
            return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}