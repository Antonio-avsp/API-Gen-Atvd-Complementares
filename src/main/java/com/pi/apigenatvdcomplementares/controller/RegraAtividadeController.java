package com.pi.apigenatvdcomplementares.controller;

import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.RegraAtividade;
import com.pi.apigenatvdcomplementares.service.RegraAtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/regras")
public class RegraAtividadeController {

    @Autowired
    private RegraAtividadeService service;

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<RegraAtividade>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(service.listarPorCurso(cursoId));
    }

    @PostMapping
    public ResponseEntity<RegraAtividade> salvar(@RequestBody Map<String, Object> body) {
        RegraAtividade regra = new RegraAtividade();
        regra.setId((String) body.get("id"));
        regra.setArea((String) body.get("area"));
        regra.setLimiteHoras((Integer) body.get("limiteHoras"));
        regra.setExigeComprovante((Boolean) body.get("exigeComprovante"));

        Curso curso = new Curso();
        curso.setId(Long.valueOf(body.get("cursoId").toString()));
        regra.setCurso(curso);

        return ResponseEntity.ok(service.salvar(regra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegraAtividade> atualizar(@PathVariable String id, @RequestBody Map<String, Object> body) {
        RegraAtividade regra = new RegraAtividade();
        regra.setId(id);
        regra.setArea((String) body.get("area"));
        regra.setLimiteHoras((Integer) body.get("limiteHoras"));
        regra.setExigeComprovante((Boolean) body.get("exigeComprovante"));

        Curso curso = new Curso();
        curso.setId(Long.valueOf(body.get("cursoId").toString()));
        regra.setCurso(curso);

        return ResponseEntity.ok(service.salvar(regra));
    }
}