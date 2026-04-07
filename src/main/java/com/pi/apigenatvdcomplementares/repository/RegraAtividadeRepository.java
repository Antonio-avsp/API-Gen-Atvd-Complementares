package com.pi.apigenatvdcomplementares.repository;

import com.pi.apigenatvdcomplementares.models.RegraAtividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RegraAtividadeRepository extends JpaRepository<RegraAtividade, String> {

    // Método para procurar todas as regras associadas a um curso específico
    List<RegraAtividade> findByCursoId(Long cursoId);
}