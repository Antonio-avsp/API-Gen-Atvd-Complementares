package com.pi.apigenatvdcomplementares.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pi.apigenatvdcomplementares.dto.AlunoCsvResponseDTO;
import com.pi.apigenatvdcomplementares.service.AlunoCsvService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controller para cadastro em lote de alunos via arquivo CSV.
 *
 * Formato esperado do CSV:
 * nome,email,matricula,cursoId
 * João Silva,joao@email.com,2024001,1
 * Maria Souza,maria@email.com,2024002,2
 *
 * Regras:
 * - Primeira linha deve ser o cabeçalho (é ignorada no processamento)
 * - Separador obrigatório: vírgula (,)
 * - Se o e-mail não existir no sistema, o usuário é criado automaticamente
 * com perfil ALUNO e senha padrão igual à matrícula
 * - Falhas em linhas individuais NÃO interrompem o processamento das demais
 * - Apenas COORDENADOR e SUPER_ADMIN podem acessar este endpoint
 */
@RestController
@RequestMapping("/alunos/lote")
public class AlunoCsvController {

    @Autowired
    private AlunoCsvService alunoCsvService;

    /**
     * POST /alunos/lote/csv
     *
     * Recebe um arquivo CSV e cadastra os alunos em lote.
     * Protegido: apenas SUPER_ADMIN e COORDENADOR podem acessar.
     *
     * @param arquivo arquivo .csv enviado via multipart/form-data (campo:
     *                "arquivo")
     * @return resumo do processamento com detalhes de cada linha
     */
    @Operation(summary = "Cadastro em lote de alunos via CSV", description = "Recebe um arquivo CSV e cadastra os alunos em lote. "
            + "Se o e-mail não estiver cadastrado no sistema, o usuário é criado automaticamente "
            + "com perfil ALUNO e senha padrão igual à matrícula. "
            + "Cada linha com erro é registrada no retorno sem interromper as demais. "
            + "Formato esperado: nome,email,matricula,cursoId (com cabeçalho na primeira linha).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "CSV processado com sucesso — todos os alunos cadastrados."),
            @ApiResponse(responseCode = "207", description = "CSV processado com falhas parciais — verifique 'detalhes' no retorno."),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou formato incorreto.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Sem permissão para realizar esta operação.", content = @Content)
    })
    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cadastrarEmLote(
            @RequestParam("arquivo") MultipartFile arquivo) {

        try {
            AlunoCsvResponseDTO resultado = alunoCsvService.processarCsv(arquivo);

            // 207 Multi-Status se houve falhas parciais, 200 se tudo foi bem
            HttpStatus status = resultado.getFalhas() > 0
                    ? HttpStatus.MULTI_STATUS
                    : HttpStatus.OK;

            return ResponseEntity.status(status).body(resultado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("erro", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro inesperado ao processar o arquivo: " + e.getMessage()));
        }
    }
}