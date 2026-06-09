package com.pi.apigenatvdcomplementares.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Submissao;

@Repository
public interface SubmissaoRepository extends JpaRepository<Submissao, Long> {
    List<Submissao> findAllByAlunoMatricula(String matricula); // Método para encontrar todas as submissões de um aluno pela matrícula

    boolean existsByAlunoMatricula(String matricula); // Método para verificar se existem submissões para um aluno com a matrícula fornecida

    List<Submissao> findByStatus(StatusSubmissao pendente); // Método para encontrar todas as submissões com um status específico

    // ── Histórico de Submissões ──────────────────────────────────────────────

    /**
     * Histórico do aluno autenticado (perfil ALUNO).
     * Filtra pelo usuarioId do aluno e ordena da mais recente para a mais antiga.
     */
    List<Submissao> findByAlunoUsuarioIdOrderByDataSubmissaoDesc(Long usuarioId);

    /**
     * Histórico de todas as submissões dos cursos coordenados (perfil COORDENADOR).
     * Recebe a lista de cursoIds que o coordenador supervisiona.
     */
    List<Submissao> findByCursoIdInOrderByDataSubmissaoDesc(List<Long> cursoIds);

    /** Histórico completo, mais recente primeiro (perfil SUPER_ADMIN). */
    List<Submissao> findAllByOrderByDataSubmissaoDesc();

}