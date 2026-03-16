package com.pi.apigenatvdcomplementares.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.CoordenadorCurso;

@Repository
public interface CoordenadorRepository extends JpaRepository<CoordenadorCurso, Long> {

    Optional<CoordenadorCurso> findByEmail(String email);

    List<CoordenadorCurso> findByCoordenadorNome(String nome);
}