package com.pi.apigenatvdcomplementares.repository;

import com.pi.apigenatvdcomplementares.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByEmailAndCodigoAndUsadoFalse(String email, String codigo);

    void deleteByEmail(String email);
}