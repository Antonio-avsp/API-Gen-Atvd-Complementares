package com.pi.apigenatvdcomplementares.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String sendgridApiKey;

    @Value("${sendgrid.from.email:jorgeafigueredo2@gmail.com}")
    private String fromEmail;

    @Value("${sendgrid.from.name:Sistema Senac}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String SENDGRID_URL = "https://api.sendgrid.com/v3/mail/send";

    @Async
    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(sendgridApiKey);

            Map<String, Object> body = new HashMap<>();

            // from
            Map<String, String> from = new HashMap<>();
            from.put("email", fromEmail);
            from.put("name", fromName);
            body.put("from", from);

            // to
            Map<String, String> to = new HashMap<>();
            to.put("email", destinatario);
            Map<String, Object> personalization = new HashMap<>();
            personalization.put("to", List.of(to));
            body.put("personalizations", List.of(personalization));

            body.put("subject", assunto);

            // content
            Map<String, String> content = new HashMap<>();
            content.put("type", "text/html");
            content.put("value", corpoHtml);
            body.put("content", List.of(content));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(SENDGRID_URL, request, String.class);

        } catch (Exception e) {
            System.err.println("Erro ao enviar email para " + destinatario + ": " + e.getMessage());
        }
    }

    // ── 1. Confirmação de envio ───────────────────────────────────────────────

    public void enviarConfirmacaoSubmissao(
            String emailAluno,
            String nomeAluno,
            String nomeCurso,
            String tituloAtividade,
            Integer horas,
            Long protocolo,
            String dataEnvio) {

        String assunto = "Atividade recebida — " + tituloAtividade;
        String html = buildEmailBase(
                nomeCurso,
                nomeAluno,
                "Sua atividade complementar foi <strong>recebida com sucesso</strong> e está aguardando avaliação do coordenador.",
                buildTabela(new String[][]{
                        {"Atividade", tituloAtividade},
                        {"Carga horária", horas + "h"},
                        {"Protocolo", "#" + protocolo},
                        {"Data de envio", dataEnvio}
                }),
                "<p style='font-size:13px;color:#6b7280;line-height:1.6;margin:0;'>" +
                "Você será notificado por email assim que a atividade for avaliada. " +
                "Acompanhe o status pelo sistema.</p>",
                "Recebida", "#d1fae5", "#065f46"
        );
        enviarEmail(emailAluno, assunto, html);
    }

    // ── 2. Aprovação ─────────────────────────────────────────────────────────

    public void enviarAprovacao(
            String emailAluno,
            String nomeAluno,
            String nomeCurso,
            String tituloAtividade,
            Integer horas,
            String nomeCoord,
            String feedback) {

        String assunto = "Atividade aprovada — " + tituloAtividade;
        String feedbackHtml = (feedback != null && !feedback.isBlank())
                ? "<div style='background:#d1fae5;border-radius:8px;padding:12px 16px;margin-top:12px;'>" +
                  "<p style='font-size:12px;color:#065f46;font-weight:500;margin:0 0 4px;'>Feedback do coordenador</p>" +
                  "<p style='font-size:13px;color:#065f46;margin:0;line-height:1.6;'>" + feedback + "</p></div>"
                : "";

        String html = buildEmailBase(
                nomeCurso,
                nomeAluno,
                "Sua atividade foi <strong style='color:#065f46;'>aprovada</strong> pelo coordenador e as horas já foram computadas no seu histórico.",
                buildTabela(new String[][]{
                        {"Atividade", tituloAtividade},
                        {"Horas aprovadas", "<span style='color:#065f46;font-weight:600;'>" + horas + "h</span>"},
                        {"Coordenador", nomeCoord}
                }) + feedbackHtml,
                "<p style='font-size:13px;color:#6b7280;line-height:1.6;margin:0;'>Continue enviando suas atividades para completar a carga horária exigida.</p>",
                "Aprovada", "#d1fae5", "#065f46"
        );
        enviarEmail(emailAluno, assunto, html);
    }

    // ── 3. Reprovação ─────────────────────────────────────────────────────────

    public void enviarReprovacao(
            String emailAluno,
            String nomeAluno,
            String nomeCurso,
            String tituloAtividade,
            String nomeCoord,
            String feedback) {

        String assunto = "Atividade reprovada — " + tituloAtividade;
        String motivoHtml = (feedback != null && !feedback.isBlank())
                ? "<div style='background:#fee2e2;border-radius:8px;padding:12px 16px;margin-top:12px;'>" +
                  "<p style='font-size:12px;color:#991b1b;font-weight:500;margin:0 0 4px;'>Motivo da reprovação</p>" +
                  "<p style='font-size:13px;color:#7f1d1d;margin:0;line-height:1.6;'>" + feedback + "</p></div>"
                : "";

        String html = buildEmailBase(
                nomeCurso,
                nomeAluno,
                "Sua atividade foi <strong style='color:#991b1b;'>reprovada</strong> pelo coordenador. " +
                "Verifique o motivo abaixo e reenvie com as correções necessárias.",
                buildTabela(new String[][]{
                        {"Atividade", tituloAtividade},
                        {"Coordenador", nomeCoord}
                }) + motivoHtml,
                "<p style='font-size:13px;color:#6b7280;line-height:1.6;margin:0;'>Acesse o sistema para corrigir e reenviar a atividade.</p>",
                "Reprovada", "#fee2e2", "#991b1b"
        );
        enviarEmail(emailAluno, assunto, html);
    }

    // ── Builders ──────────────────────────────────────────────────────────────

    private String buildEmailBase(
            String nomeCurso,
            String nomeAluno,
            String mensagem,
            String conteudo,
            String rodapeTexto,
            String badgeTexto,
            String badgeBg,
            String badgeColor) {

        return "<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width,initial-scale=1'></head><body " +
               "style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;'>" +
               "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f3f4f6;padding:32px 16px;'>" +
               "<tr><td align='center'>" +
               "<table width='560' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;overflow:hidden;'>" +

               "<tr><td style='background:#1a56db;padding:32px 32px 24px;text-align:center;'>" +
               "<div style='display:inline-block;background:rgba(255,255,255,0.25);" +
               "border-radius:12px;padding:10px 24px;margin-bottom:20px;'>" +
               "<span style='font-size:22px;font-weight:800;color:#ffffff;" +
               "letter-spacing:2px;font-family:Arial,sans-serif;'>SENAC</span>" +
               "</div>" +
               "<p style='color:#ffffff;font-size:18px;font-weight:600;margin:0;'>" +
               "Sistema de Atividades Complementares</p>" +
               "<p style='color:rgba(255,255,255,0.75);font-size:13px;margin:4px 0 0;'>" +
               "Senac — " + nomeCurso + "</p>" +
               "</td></tr>" +

               "<tr><td style='padding:32px;'>" +
               "<div style='text-align:center;margin-bottom:24px;'>" +
               "<span style='display:inline-block;background:" + badgeBg + ";color:" + badgeColor + ";" +
               "font-size:13px;font-weight:600;padding:6px 20px;border-radius:999px;'>" +
               badgeTexto + "</span></div>" +
               "<p style='font-size:15px;color:#111827;margin:0 0 8px;'>Olá, <strong>" + nomeAluno + "</strong></p>" +
               "<p style='font-size:14px;color:#6b7280;line-height:1.7;margin:0 0 24px;'>" + mensagem + "</p>" +
               conteudo +
               "<div style='margin-top:24px;'>" + rodapeTexto + "</div>" +
               "</td></tr>" +

               "<tr><td style='border-top:1px solid #f3f4f6;padding:16px 32px;text-align:center;'>" +
               "<p style='font-size:12px;color:#9ca3af;margin:0;'>Este é um email automático. Não responda.</p>" +
               "</td></tr>" +

               "</table></td></tr></table></body></html>";
    }

    private String buildTabela(String[][] linhas) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style='background:#f9fafb;border-radius:8px;padding:16px 20px;'>");
        sb.append("<table style='width:100%;border-collapse:collapse;font-size:13px;'>");
        for (String[] linha : linhas) {
            sb.append("<tr>")
              .append("<td style='color:#6b7280;padding:5px 0;width:45%;'>" + linha[0] + "</td>")
              .append("<td style='color:#111827;font-weight:600;padding:5px 0;'>" + linha[1] + "</td>")
              .append("</tr>");
        }
        sb.append("</table></div>");
        return sb.toString();
    }
}