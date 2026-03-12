package com.pi.apigenatvdcomplementares.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario salvarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Já existe um usuário com este email.");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id));
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email));
    }

    public Usuario atualizarUsuario(String id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarPorId(id);

        if (!usuarioExistente.getEmail().equals(usuarioAtualizado.getEmail()) &&
                usuarioRepository.existsByEmail(usuarioAtualizado.getEmail())) {
            throw new RuntimeException("Já existe um usuário com este email.");
        }
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setPerfil(usuarioAtualizado.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(String id) {
        Usuario usuarioExistente = buscarPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }
}
