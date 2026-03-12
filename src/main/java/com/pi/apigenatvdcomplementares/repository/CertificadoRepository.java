package com.pi.apigenatvdcomplementares.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pi.apigenatvdcomplementares.models.Certificado;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    List<Certificado> findBySubmissaoId(Long submissaoId);

    boolean existsBySubmissaoId(Long submissaoId);
}