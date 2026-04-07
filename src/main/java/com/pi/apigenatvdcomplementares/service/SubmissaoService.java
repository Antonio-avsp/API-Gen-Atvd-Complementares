package com.pi.apigenatvdcomplementares.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.dto.SubmissaoRequestDTO;
import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.Submissao;
import com.pi.apigenatvdcomplementares.repository.SubmissaoRepository;

@Service
public class SubmissaoService {

    @Autowired
    private SubmissaoRepository submissaoRepository;

    // 1. Alterado para receber o DTO
    public Submissao criarSubmissao(SubmissaoRequestDTO dto) {
        
        Submissao submissao = new Submissao();
        submissao.setTitulo(dto.getTitulo());
        submissao.setDescricao(dto.getDescricao());
        submissao.setHoras(dto.getHoras());

        // Mapeando Aluno e Curso apenas pelos IDs
        Aluno aluno = new Aluno();
        aluno.setUsuarioId(dto.getAlunoId()); // Ajuste caso sua entidade Aluno use "setId"
        submissao.setAluno(aluno);

        Curso curso = new Curso();
        curso.setId(dto.getCursoId());
        submissao.setCurso(curso);

        // Configurações iniciais de Status e Data
        submissao.setStatus(StatusSubmissao.PENDENTE);
        submissao.setDataSubmissao(LocalDateTime.now());
        submissao.setHistoricoStatus(new HashSet<>());
        submissao.getHistoricoStatus().add(StatusSubmissao.PENDENTE);

        // Removemos a validação que exigia o certificado na criação.
        // O certificado será atrelado a esta submissão no passo seguinte!

        return submissaoRepository.save(submissao);
    }

    // 2. Renomeado para listarTodas (para bater com o seu Controller)
    public List<Submissao> listarTodas() {
        return submissaoRepository.findAll();
    }

    public Submissao buscarPorId(Long id) {
        return submissaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submissão não encontrada"));
    }

    public List<Submissao> listarPorAlunoMatricula(String matricula) {
        return submissaoRepository.findAllByAlunoMatricula(matricula);
    }

    public List<Submissao> listarPendentes() {
        return submissaoRepository.findByStatus(StatusSubmissao.PENDENTE);
    }

    public Submissao aprovarSubmissao(Long id) {
        return alterarStatusSubmissao(id, StatusSubmissao.APROVADA);
    }

    public Submissao rejeitarSubmissao(Long id) {
        return alterarStatusSubmissao(id, StatusSubmissao.REPROVADA);
    }

    public Submissao alterarStatusSubmissao(Long id, StatusSubmissao novoStatus) {
        Submissao submissao = buscarPorId(id);

        if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
            throw new IllegalStateException("Essa submissão já foi analisada");
        }

        submissao.setStatus(novoStatus);
        submissao.getHistoricoStatus().add(novoStatus);

        return submissaoRepository.save(submissao);
    }

    // 3. Ajuste rápido no Deletar para bater com o Controller básico que fizemos
    // (Mais tarde você pode voltar a receber o Usuario aqui quando implementar segurança com Token)
    public void deletar(Long id) {
        Submissao submissaoExistente = buscarPorId(id);

        if (submissaoExistente.getStatus() != StatusSubmissao.PENDENTE) {
            throw new IllegalStateException("Não é possível excluir uma submissão já analisada");
        }

        submissaoRepository.delete(submissaoExistente);
    }
}