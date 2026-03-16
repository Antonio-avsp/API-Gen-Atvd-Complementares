package com.pi.apigenatvdcomplementares;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ApigenatvdcomplementaresApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApigenatvdcomplementaresApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegadorAposInicio() {
        String url = "http://localhost:8080/swagger-ui/index.html"; // URL padrão do Swagger
        System.out.println("🚀 Aplicação iniciada! Tentando abrir o Swagger em: " + url);

        try {
            // Verifica se o ambiente suporta operação de Desktop
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(new URI(url));
                }
            } else {
                // Fallback para ambientes onde Desktop não é suportado
                Runtime runtime = Runtime.getRuntime();
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    runtime.exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    runtime.exec("xdg-open " + url);
                }
            }
        } catch (IOException | URISyntaxException e) {
            // Se falhar (ex: rodando em servidor headless), apenas ignora e segue a vida
            System.err.println("⚠️ Não foi possível abrir o navegador automaticamente: " + e.getMessage());
        }
    }

}
