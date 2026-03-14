package com.pi.apigenatvdcomplementares.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Certificado;
import com.pi.apigenatvdcomplementares.repository.CertificadoRepository;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    public void validarCertificado(Certificado certificado) {
        if (certificado.getNomeArquivo() == null || certificado.getNomeArquivo().isBlank()) {
            throw new IllegalArgumentException("O nome do arquivo não pode estar vazio");
        }

        if (certificado.getUrlArquivo() == null || certificado.getUrlArquivo().isBlank()) {
            throw new IllegalArgumentException("O url não pode ser vazio");

        }

        if (certificado.getSubmissao() == null) {
            throw new IllegalArgumentException("O certificado deve estar vinculado a um submissão.");
        }
    }

    private void verificarSubmissaoPendente(Certificado certificado, String acao) {
        if (certificado.getSubmissao().getStatus() != StatusSubmissao.PENDENTE) {
            throw new IllegalStateException(
                    "Não é possível " + acao + " o certificado de uma submissão já analisada");
        }
    }

    public Certificado anexarCertificado(Certificado certificado) {
        validarCertificado(certificado);

        verificarSubmissaoPendente(certificado, "anexar");

        return certificadoRepository.save(certificado);
    }

    public List<Certificado> listarCertificados() {
        return certificadoRepository.findAll();
    }

    public Certificado buscarPorId(Long id) {
        return certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado não encontrado"));

    }

    public Certificado atualizarCertificado(Certificado certificadoAtualizado, Long id) {

        Certificado certificadoExiste = buscarPorId(id);

        verificarSubmissaoPendente(certificadoExiste, "atualizar");

        certificadoExiste.setNomeArquivo(certificadoAtualizado.getNomeArquivo());
        certificadoExiste.setUrlArquivo(certificadoAtualizado.getUrlArquivo());

        validarCertificado(certificadoExiste);

        return certificadoRepository.save(certificadoExiste);

    }

    public void deletarCertificado(Long id) {
        Certificado certificado = buscarPorId(id);

        verificarSubmissaoPendente(certificado, "deletar");

        certificadoRepository.delete(certificado);
    }

}
