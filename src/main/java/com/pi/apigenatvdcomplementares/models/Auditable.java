package com.pi.apigenatvdcomplementares.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;


import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    // MÉTODOS //

    @PrePersist // Executa automaticamente antes de persistir um novo registro no banco de
                // dados.
    public void prePersist() {
        // Define a data e hora atual para ambos os campos criadoEm e atualizadoEm
        // quando um novo registro é criado.
        LocalDateTime now = LocalDateTime.now();
        criadoEm = now;
        atualizadoEm = now;
    }

    @PreUpdate // Executa automaticamente antes de atualizar um registro existente.
    public void preUpdate() {
        // Atualiza apenas o campo atualizadoEm para a data e hora atual sempre que um
        // registro for modificado.
        this.atualizadoEm = LocalDateTime.now();
    }
}
