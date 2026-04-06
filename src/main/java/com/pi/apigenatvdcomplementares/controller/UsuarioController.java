package com.pi.apigenatvdcomplementares.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.pi.apigenatvdcomplementares.dto.UsuarioCreateDTO;
import com.pi.apigenatvdcomplementares.dto.UsuarioUpdateDTO;
import com.pi.apigenatvdcomplementares.dto.UsuarioDTO;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@Validated
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COORDENADOR')")
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(
            @RequestBody @Valid UsuarioCreateDTO dto,
            Authentication authentication) {

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setPerfil(dto.getPerfil());

        String emailUsuarioLogado = authentication.getName();
        Usuario usuarioLogado = usuarioService.buscarPorEmail(emailUsuarioLogado);

        Usuario novoUsuario = usuarioService.salvarUsuario(usuario, usuarioLogado);

        return ResponseEntity.status(HttpStatus.CREATED).body(new UsuarioDTO(novoUsuario));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarUsuarios()
                .stream()
                .map(UsuarioDTO::new)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/coordenadores")
    public ResponseEntity<List<UsuarioDTO>> listarCoordenadores() {
        List<UsuarioDTO> coordenadores = usuarioService.listarCoordenadores()
                .stream()
                .map(UsuarioDTO::new)
                .toList();
        return ResponseEntity.ok(coordenadores);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    // MÉTODO NOVO: Atualiza o usuário
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid UsuarioUpdateDTO dto) {
        
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dto);
        return ResponseEntity.ok(new UsuarioDTO(usuarioAtualizado));
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) { 
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}