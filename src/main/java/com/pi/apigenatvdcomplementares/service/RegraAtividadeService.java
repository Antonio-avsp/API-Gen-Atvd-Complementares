package com.pi.apigenatvdcomplementares.service;

import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.RegraAtividade;
import com.pi.apigenatvdcomplementares.repository.CursoRepository;
import com.pi.apigenatvdcomplementares.repository.RegraAtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;

@Service
public class RegraAtividadeService {

    @Autowired
    private RegraAtividadeRepository repository;

    @Autowired
    private CursoRepository cursoRepository;

    public List<RegraAtividade> listarPorCurso(Long cursoId) {
        return repository.findByCursoId(cursoId);
    }

    @Transactional
    public RegraAtividade salvar(RegraAtividade regra) {
        RegraAtividade salva = repository.save(regra);
        atualizarCargaHorariaCurso(regra.getCurso().getId());
        return salva;
    }

    @Transactional
    public void deletar(Long id) {
        RegraAtividade regra = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regra não encontrada."));
        Long cursoId = regra.getCurso().getId();
        repository.deleteById(id);
        atualizarCargaHorariaCurso(cursoId);
    }

    /**
     * Recalcula a carga horária mínima do curso somando os limites de todas as regras cadastradas.
     * Chamado automaticamente ao salvar ou deletar uma regra.
     */
    private void atualizarCargaHorariaCurso(Long cursoId) {
        List<RegraAtividade> regras = repository.findByCursoId(cursoId);

        int totalHoras = regras.stream()
                .mapToInt(r -> r.getLimiteHoras() != null ? r.getLimiteHoras() : 0)
                .sum();

        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado."));

        curso.setCargaHorariaMinima(totalHoras);
        cursoRepository.save(curso);
    }
}