package com.pi.apigenatvdcomplementares.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email); // Método para encontrar um usuário pelo email

    Optional<Usuario> findById(Long id); // Método para encontrar um usuário pelo ID

    boolean existsByEmail(String email); // Método para verificar se um usuário com o email fornecido já existe
}
