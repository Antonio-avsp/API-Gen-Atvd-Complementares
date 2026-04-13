package com.pi.apigenatvdcomplementares.service;

import com.pi.apigenatvdcomplementares.models.PasswordResetToken;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.PasswordResetTokenRepository;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Gera código de 6 dígitos e envia por email.
     * Não revela se o email existe ou não (segurança).
     */
    @Transactional
    public void solicitarRecuperacao(String email) {
        // Verifica se email existe — mas não informa ao front
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            // Remove tokens anteriores
            tokenRepository.deleteByEmail(email);

            // Gera código de 6 dígitos
            String codigo = String.format("%06d", new Random().nextInt(999999));

            // Expira em 15 minutos
            LocalDateTime expiracao = LocalDateTime.now().plusMinutes(15);

            tokenRepository.save(new PasswordResetToken(email, codigo, expiracao));

            emailService.enviarCodigoRecuperacao(email, usuario.getNome(), codigo);
        });
    }

    /**
     * Valida se o código está correto e não expirou.
     */
    public boolean validarCodigo(String email, String codigo) {
        return tokenRepository.findByEmailAndCodigoAndUsadoFalse(email, codigo)
                .map(token -> {
                    if (token.getExpiracao().isBefore(LocalDateTime.now())) {
                        return false; // expirado
                    }
                    return true;
                })
                .orElse(false);
    }

    /**
     * Redefine a senha após validação do código.
     */
    @Transactional
    public void redefinirSenha(String email, String codigo, String novaSenha) {
        PasswordResetToken token = tokenRepository
                .findByEmailAndCodigoAndUsadoFalse(email, codigo)
                .orElseThrow(() -> new RuntimeException("Código inválido ou expirado."));

        if (token.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Código expirado. Solicite um novo.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        // Marca token como usado
        token.setUsado(true);
        tokenRepository.save(token);

        // Envia email de confirmação
        emailService.enviarConfirmacaoSenhaAlterada(email, usuario.getNome());
    }
}