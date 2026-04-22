package com.pi.apigenatvdcomplementares.repository;

import com.pi.apigenatvdcomplementares.models.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Optional<Aluno> findByMatricula(String matricula);

    List<Aluno> findByTurmaId(Long turmaId);
}