package com.pi.apigenatvdcomplementares.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByNome(String nome); // Método para encontrar um curso pelo nome

    boolean existsByNome(String nome);

}
