package com.pi.apigenatvdcomplementares.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;

@Repository
public interface CoordenadorRepository extends JpaRepository<CoordenadorCurso, Long> {

    Optional<CoordenadorCurso> findByEmail(String email);

    List<CoordenadorCurso> findByNomeContainingIgnoreCase(String nome);

    /** Verifica se um coordenador específico já está vinculado a um curso */
    boolean existsByCoordenadorIdAndCursoId(Long usuarioId, Long cursoId);

    /**
     * ✅ Novo: verifica se um curso já tem QUALQUER coordenador vinculado.
     * Garante a regra: um curso só pode ter um coordenador.
     */
    boolean existsByCursoId(Long cursoId);
}