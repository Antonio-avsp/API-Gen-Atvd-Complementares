package com.pi.apigenatvdcomplementares.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pi.apigenatvdcomplementares.models.Turma;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    boolean existsByCodigo(String codigo);
    List<Turma> findByCursoId(Long cursoId);
}
