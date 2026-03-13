package com.pi.apigenatvdcomplementares.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByNomeIgnoreCase(String nome); // Método para encontrar um curso pelo nome ignorando maiúsculas e minúsculas

    boolean existsByNome(String nome);

}
