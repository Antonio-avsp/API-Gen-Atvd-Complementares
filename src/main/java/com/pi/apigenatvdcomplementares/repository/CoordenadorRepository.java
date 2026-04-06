package com.pi.apigenatvdcomplementares.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;

@Repository
public interface CoordenadorRepository extends JpaRepository<CoordenadorCurso, Long> {

    // Procura um coordenador pelo email
    Optional<CoordenadorCurso> findByEmail(String email);

    // Procura coordenadores cujo nome contenha o texto pesquisado
    List<CoordenadorCurso> findByNomeContainingIgnoreCase(String nome);

    // Método crucial para verificar se um coordenador específico tem permissão sobre um curso
    // Note que usamos 'CoordenadorId' porque o campo na entidade CoordenadorCurso chama-se 'coordenador'
    boolean existsByCoordenadorIdAndCursoId(Long usuarioId, Long cursoId);
}