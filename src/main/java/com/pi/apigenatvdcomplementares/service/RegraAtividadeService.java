package com.pi.apigenatvdcomplementares.service;

import com.pi.apigenatvdcomplementares.models.RegraAtividade;
import com.pi.apigenatvdcomplementares.repository.RegraAtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RegraAtividadeService {

    @Autowired
    private RegraAtividadeRepository repository;

    public List<RegraAtividade> listarPorCurso(Long cursoId) {
        return repository.findByCursoId(cursoId);
    }

    public RegraAtividade salvar(RegraAtividade regra) {
        return repository.save(regra);
    }

    public void deletar(String id) {
        repository.deleteById(id);
    }
}