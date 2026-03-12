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
            throw new RuntimeException("Já existe um usuário com este email."); // Verifica se já existe um usuário com o mesmo email antes de salvar
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com id: " + id)); // Método para buscar usuário por ID
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com email: " + email)); // Método para buscar usuário por email
    }

    public Usuario atualizarUsuario(String id, Usuario usuarioAtualizado) { // Método para atualizar um usuário existente
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

    public void deletarUsuario(String id) { // Método para deletar um usuário por ID
        Usuario usuarioExistente = buscarPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }
}
