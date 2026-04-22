package com.pi.apigenatvdcomplementares.controller;

import com.pi.apigenatvdcomplementares.dto.AlunoDTO;
import com.pi.apigenatvdcomplementares.dto.TurmaCreateDTO;
import com.pi.apigenatvdcomplementares.models.Aluno;
import com.pi.apigenatvdcomplementares.models.AlunoCurso;
import com.pi.apigenatvdcomplementares.models.Turma;
import com.pi.apigenatvdcomplementares.repository.AlunoRepository;
import com.pi.apigenatvdcomplementares.service.TurmaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/turmas")
public class TurmaController {

    @Autowired
    private TurmaService turmaService;

    @Autowired
    private AlunoRepository alunoRepository;

    // ── CRUD de Turmas ────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Turma> criarTurma(@RequestBody TurmaCreateDTO dto,
                                            Authentication authentication) {
        Turma turma = turmaService.criarTurma(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(turma);
    }

    @GetMapping
    public ResponseEntity<List<Turma>> listarTodas() {
        return ResponseEntity.ok(turmaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Turma> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turmaService.buscarPorId(id));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Turma>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(turmaService.listarPorCurso(cursoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turma> atualizarTurma(@PathVariable Long id,
                                                @RequestBody TurmaCreateDTO dto,
                                                Authentication authentication) {
        Turma turmaAtualizada = turmaService.atualizarTurma(id, dto, authentication.getName());
        return ResponseEntity.ok(turmaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTurma(@PathVariable Long id,
                                             Authentication authentication) {
        turmaService.deletarTurma(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ── Gerenciamento de Alunos na Turma ──────────────────────────────────────

    /**
     * Lista os alunos vinculados a uma turma.
     * GET /turmas/{turmaId}/alunos
     */
    @GetMapping("/{turmaId}/alunos")
    public ResponseEntity<?> listarAlunos(@PathVariable Long turmaId) {
        List<AlunoDTO> alunos = alunoRepository.findByTurmaId(turmaId)
                .stream()
                .map(AlunoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alunos);
    }

    /**
     * Vincula um aluno à turma com validação de curso.
     * PATCH /turmas/{turmaId}/alunos/{alunoId}
     */
    @Transactional
    @PatchMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<?> vincularAluno(
            @PathVariable Long turmaId,
            @PathVariable Long alunoId) {

        Turma turma = turmaService.buscarPorId(turmaId);

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        // Validação: aluno deve estar matriculado no curso da turma
        Long cursoIdTurma = turma.getCurso().getId();
        boolean matriculadoNoCurso = aluno.getCursos().stream()
                .map(AlunoCurso::getCurso)
                .anyMatch(c -> c.getId().equals(cursoIdTurma));

        if (!matriculadoNoCurso) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "O aluno não está matriculado no curso '" + turma.getCurso().getNome() + "' desta turma."
            ));
        }

        aluno.setTurma(turma);
        alunoRepository.save(aluno);

        return ResponseEntity.ok(Map.of("mensagem", "Aluno vinculado com sucesso."));
    }

    /**
     * Remove o vínculo de um aluno com a turma.
     * DELETE /turmas/{turmaId}/alunos/{alunoId}
     */
    @Transactional
    @DeleteMapping("/{turmaId}/alunos/{alunoId}")
    public ResponseEntity<?> desvincularAluno(
            @PathVariable Long turmaId,
            @PathVariable Long alunoId) {

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        if (aluno.getTurma() == null || !aluno.getTurma().getId().equals(turmaId)) {
            return ResponseEntity.badRequest().body(Map.of(
                "erro", "Aluno não está vinculado a esta turma."
            ));
        }

        aluno.setTurma(null);
        alunoRepository.save(aluno);

        return ResponseEntity.ok(Map.of("mensagem", "Aluno removido da turma com sucesso."));
    }
}