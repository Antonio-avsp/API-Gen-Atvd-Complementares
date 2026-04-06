package com.pi.apigenatvdcomplementares.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(Long id);

    boolean existsByEmail(String email);

    // NOVO: Busca usuários por perfil
    List<Usuario> findByPerfil(PerfilUsuario perfil); 
}