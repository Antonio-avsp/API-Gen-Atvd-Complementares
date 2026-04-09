package com.pi.apigenatvdcomplementares.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Habilita o processamento assíncrono com @Async.
 * Usado pelo EmailService para não bloquear a thread principal ao enviar emails.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}