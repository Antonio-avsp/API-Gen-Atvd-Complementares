package com.pi.apigenatvdcomplementares.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

//  Resultado do processamento de cadastro em lote de alunos via CSV.
//  Retorna o resumo da operação: quantos foram cadastrados, quantos falharam
//  e os detalhes de cada linha processada.

@Getter
@Setter
public class AlunoCsvResponseDTO {

    private int totalLinhas;
    private int sucessos;
    private int falhas;
    private List<LinhaResultado> detalhes = new ArrayList<>();

    public void adicionarSucesso(int linha, String email, String matricula) {
        detalhes.add(new LinhaResultado(linha, email, matricula, true, null));
        sucessos++;
        totalLinhas++;
    }

    public void adicionarFalha(int linha, String email, String matricula, String motivo) {
        detalhes.add(new LinhaResultado(linha, email, matricula, false, motivo));
        falhas++;
        totalLinhas++;
    }

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LinhaResultado {
        private int linha;
        private String email;
        private String matricula;
        private boolean sucesso;
        private String motivo;

        public LinhaResultado(int linha, String email, String matricula, boolean sucesso, String motivo) {
            this.linha = linha;
            this.email = email;
            this.matricula = matricula;
            this.sucesso = sucesso;
            this.motivo = motivo;
        }
    }
}