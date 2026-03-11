package com.pi.apigenatvdcomplementares.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_registros_acao")
@Getter
@Setter
@NoArgsConstructor
public class RegistroAcao extends Auditable {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "acao", nullable = false, length = 255)
    private String acao;

    @Column(name = "detalhes", length = 1000)
    private String detalhes;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}