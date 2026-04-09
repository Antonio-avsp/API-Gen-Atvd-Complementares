package com.pi.apigenatvdcomplementares.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    // ── Envio genérico ────────────────────────────────────────────────────────

    @Async
    public void enviarEmail(String destinatario, String assunto, String corpoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente, "Sistema Senac — Atividades Complementares");
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(message);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
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

        String assunto = "✅ Atividade recebida — " + tituloAtividade;
        String html = buildEmailBase(
                "Submissão recebida",
                "#1a56db",
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
                "Recebida",
                "#d1fae5",
                "#065f46"
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

        String assunto = "🎉 Atividade aprovada — " + tituloAtividade;
        String feedbackHtml = (feedback != null && !feedback.isBlank())
                ? "<div style='background:#d1fae5;border-radius:8px;padding:12px 16px;margin-top:12px;'>" +
                  "<p style='font-size:12px;color:#065f46;font-weight:500;margin:0 0 4px;'>Feedback do coordenador</p>" +
                  "<p style='font-size:13px;color:#065f46;margin:0;line-height:1.6;'>" + feedback + "</p></div>"
                : "";

        String html = buildEmailBase(
                "Atividade aprovada",
                "#1a56db",
                nomeCurso,
                nomeAluno,
                "Sua atividade foi <strong style='color:#065f46;'>aprovada</strong> pelo coordenador e as horas já foram computadas no seu histórico.",
                buildTabela(new String[][]{
                        {"Atividade", tituloAtividade},
                        {"Horas aprovadas", "<span style='color:#065f46;font-weight:600;'>" + horas + "h</span>"},
                        {"Coordenador", nomeCoord}
                }) + feedbackHtml,
                "<p style='font-size:13px;color:#6b7280;line-height:1.6;margin:0;'>" +
                "Continue enviando suas atividades para completar a carga horária exigida.</p>",
                "Aprovada",
                "#d1fae5",
                "#065f46"
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

        String assunto = "❌ Atividade reprovada — " + tituloAtividade;
        String motivoHtml = (feedback != null && !feedback.isBlank())
                ? "<div style='background:#fee2e2;border-radius:8px;padding:12px 16px;margin-top:12px;'>" +
                  "<p style='font-size:12px;color:#991b1b;font-weight:500;margin:0 0 4px;'>Motivo da reprovação</p>" +
                  "<p style='font-size:13px;color:#7f1d1d;margin:0;line-height:1.6;'>" + feedback + "</p></div>"
                : "";

        String html = buildEmailBase(
                "Atividade reprovada",
                "#1a56db",
                nomeCurso,
                nomeAluno,
                "Sua atividade foi <strong style='color:#991b1b;'>reprovada</strong> pelo coordenador. " +
                "Verifique o motivo abaixo e reenvie com as correções necessárias.",
                buildTabela(new String[][]{
                        {"Atividade", tituloAtividade},
                        {"Coordenador", nomeCoord}
                }) + motivoHtml,
                "<p style='font-size:13px;color:#6b7280;line-height:1.6;margin:0;'>" +
                "Acesse o sistema para corrigir e reenviar a atividade.</p>",
                "Reprovada",
                "#fee2e2",
                "#991b1b"
        );
        enviarEmail(emailAluno, assunto, html);
    }

    // ── Builders de HTML ──────────────────────────────────────────────────────

    private String buildEmailBase(
            String tituloStatus,
            String corHeader,
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

               // Header
               "<tr><td style='background:" + corHeader + ";padding:32px 32px 24px;text-align:center;'>" +
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

               // Corpo
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

               // Footer
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
              .append("<td style='color:#6b7280;padding:5px 0;width:45%;'>").append(linha[0]).append("</td>")
              .append("<td style='color:#111827;font-weight:600;padding:5px 0;'>").append(linha[1]).append("</td>")
              .append("</tr>");
        }
        sb.append("</table></div>");
        return sb.toString();
    }

    private String buildIconeCheck() {
        return "<svg width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='white' " +
               "stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'>" +
               "<polyline points='20 6 9 17 4 12'/></svg>";
    }

    private String buildIconeX() {
        return "<svg width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='white' " +
               "stroke-width='2.5' stroke-linecap='round' stroke-linejoin='round'>" +
               "<circle cx='12' cy='12' r='10'/>" +
               "<line x1='15' y1='9' x2='9' y2='15'/>" +
               "<line x1='9' y1='9' x2='15' y2='15'/></svg>";
    }
}