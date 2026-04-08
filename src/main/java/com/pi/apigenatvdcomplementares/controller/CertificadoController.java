package com.pi.apigenatvdcomplementares.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi.apigenatvdcomplementares.dto.CertificadoCreateDTO;
import com.pi.apigenatvdcomplementares.dto.CertificadoDTO;
import com.pi.apigenatvdcomplementares.models.Certificado;
import com.pi.apigenatvdcomplementares.models.Submissao;
import com.pi.apigenatvdcomplementares.service.CertificadoService;

import jakarta.validation.Valid;

@RequestMapping("/certificados")
@RestController
public class CertificadoController {

    @Autowired
    CertificadoService certificadoService;

    // Permissão controlada pelo SecurityConfig — ALUNO, COORDENADOR e SUPER_ADMIN
    @PostMapping
    public ResponseEntity<CertificadoDTO> anexar(@Valid @RequestBody CertificadoCreateDTO dto) {
        Certificado certificado = new Certificado();
        certificado.setNomeArquivo(dto.getNomeArquivo());
        certificado.setUrlArquivo(dto.getUrlArquivo());

        Submissao submissaoRef = new Submissao();
        submissaoRef.setId(dto.getSubmissaoId());
        certificado.setSubmissao(submissaoRef);

        Certificado novo = certificadoService.anexarCertificado(certificado);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CertificadoDTO(novo));
    }

    @GetMapping
    public ResponseEntity<List<CertificadoDTO>> listarTodos() {
        List<CertificadoDTO> listaLimpa = certificadoService.listarCertificados()
                .stream()
                .map(CertificadoDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(listaLimpa);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificadoDTO> buscarPorID(@PathVariable Long id) {
        Certificado certificado = certificadoService.buscarPorId(id);
        return ResponseEntity.ok(new CertificadoDTO(certificado));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CertificadoDTO> atualizarCertificado(
            @PathVariable Long id,
            @RequestBody CertificadoDTO dto) {
        Certificado atualizado = certificadoService.atualizarCertificado(dto, id);
        return ResponseEntity.ok(new CertificadoDTO(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCertificado(@PathVariable Long id) {
        certificadoService.deletarCertificado(id);
        return ResponseEntity.noContent().build();
    }
}