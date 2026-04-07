package com.pi.apigenatvdcomplementares.controller;

import com.pi.apigenatvdcomplementares.models.Curso;
import com.pi.apigenatvdcomplementares.models.ItemRegraAtividade;
import com.pi.apigenatvdcomplementares.models.RegraAtividade;
import com.pi.apigenatvdcomplementares.service.RegraAtividadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        RegraAtividade regra = buildRegra(null, body);
        return ResponseEntity.ok(service.salvar(regra));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegraAtividade> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RegraAtividade regra = buildRegra(id, body);
        return ResponseEntity.ok(service.salvar(regra));
    }

    @SuppressWarnings("unchecked")
    private RegraAtividade buildRegra(Long id, Map<String, Object> body) {
        RegraAtividade regra = new RegraAtividade();
        regra.setId(id);
        regra.setArea((String) body.get("area"));
        regra.setLimiteHoras((Integer) body.get("limiteHoras"));
        regra.setExigeComprovante((Boolean) body.get("exigeComprovante"));

        Curso curso = new Curso();
        curso.setId(Long.valueOf(body.get("cursoId").toString()));
        regra.setCurso(curso);

        // Itens da regra (descrições + aproveitamento)
        List<Map<String, String>> itensBody = (List<Map<String, String>>) body.get("itens");
        if (itensBody != null) {
            List<ItemRegraAtividade> itens = new ArrayList<>();
            for (Map<String, String> itemBody : itensBody) {
                ItemRegraAtividade item = new ItemRegraAtividade();
                item.setDescricao(itemBody.get("descricao"));
                item.setAproveitamento(itemBody.get("aproveitamento"));
                item.setExplicacao(itemBody.get("explicacao"));
                item.setRegraAtividade(regra);
                itens.add(item);
            }
            regra.setItens(itens);
        }

        return regra;
    }
}
