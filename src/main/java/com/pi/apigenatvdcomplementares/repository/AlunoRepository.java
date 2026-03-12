package com.pi.apigenatvdcomplementares.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    
    Optional<Aluno> findByMatricula(String matricula); // Método para encontrar um aluno pela matrícula

    boolean existsByMatricula(String matricula); // Método para verificar se um aluno com a matrícula fornecida já existe

    boolean existsByUsuarioId(String usuarioId); // Método para verificar se um aluno com o ID do usuário fornecido já existe
    
}
