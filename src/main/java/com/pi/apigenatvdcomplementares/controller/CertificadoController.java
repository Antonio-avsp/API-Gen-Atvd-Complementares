package com.pi.apigenatvdcomplementares.controller;

import java.util.List;

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

import com.pi.apigenatvdcomplementares.dto.CertificadoDTO;
import com.pi.apigenatvdcomplementares.models.Certificado;
import com.pi.apigenatvdcomplementares.service.CertificadoService;

import jakarta.validation.Valid;

@RequestMapping("/certificados")
@RestController
public class CertificadoController {

    @Autowired
    CertificadoService certificadoService;

    @PostMapping
    public ResponseEntity<Certificado> anexar(@Valid @RequestBody Certificado certificado) {
        Certificado novo = certificadoService.anexarCertificado(certificado);
        return ResponseEntity.status(HttpStatus.CREATED).body(novo);
    }

    @GetMapping
    public ResponseEntity<List<Certificado>> listarTodos() {
        return ResponseEntity.ok(certificadoService.listarCertificados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificado> buscarPorID(@PathVariable Long id) {
        return ResponseEntity.ok(certificadoService.buscarPorId(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Certificado> atualizarCertificado(@PathVariable Long id, @RequestBody CertificadoDTO dto) {
        return ResponseEntity.ok(certificadoService.atualizarCertificado(dto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCertificado(@PathVariable Long id) {
        certificadoService.deletarCertificado(id);
        return ResponseEntity.noContent().build();
    }
}
