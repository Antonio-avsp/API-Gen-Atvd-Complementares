package com.pi.apigenatvdcomplementares.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pi.apigenatvdcomplementares.dto.CertificadoDTO;
import com.pi.apigenatvdcomplementares.enums.StatusSubmissao;
import com.pi.apigenatvdcomplementares.models.Certificado;
import com.pi.apigenatvdcomplementares.models.Submissao;
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
            throw new IllegalArgumentException("A URL não pode estar vazia");
        }

        if (certificado.getSubmissao() == null) {
            throw new IllegalArgumentException("O certificado deve estar vinculado a uma submissão.");
        }
    }

    // Unificamos o método para verificar sempre a submissão vinculada ao certificado
    private void verificarSubmissaoPendente(Certificado certificado) {
        Submissao submissao = certificado.getSubmissao();
        
        // Se a submissão existir e o status for diferente de PENDENTE, bloqueia.
        if (submissao != null && submissao.getStatus() != null) {
            if (submissao.getStatus() != StatusSubmissao.PENDENTE) {
                throw new IllegalStateException("Não é possível alterar certificados de uma submissão já analisada (Status atual: " + submissao.getStatus() + ")");
            }
        }
    }

    public Certificado anexarCertificado(Certificado certificado) {
        validarCertificado(certificado);
        // Agora passamos o objeto correto
        verificarSubmissaoPendente(certificado); 
        return certificadoRepository.save(certificado);
    }

    public List<Certificado> listarCertificados() {
        return certificadoRepository.findAll();
    }

    public Certificado buscarPorId(Long id) {
        return certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado não encontrado"));
    }

    public Certificado atualizarCertificado(CertificadoDTO dto, Long id) {
        Certificado certificadoExiste = buscarPorId(id);

        // Verifica se a submissão ainda permite alteração
        verificarSubmissaoPendente(certificadoExiste);

        certificadoExiste.setNomeArquivo(dto.getNomeArquivo());
        certificadoExiste.setUrlArquivo(dto.getUrlArquivo());

        validarCertificado(certificadoExiste);

        return certificadoRepository.save(certificadoExiste);
    }

    public void deletarCertificado(Long id) {
        Certificado certificado = buscarPorId(id);

        // Verifica se a submissão ainda permite exclusão
        verificarSubmissaoPendente(certificado);

        certificadoRepository.delete(certificado);
    }
}