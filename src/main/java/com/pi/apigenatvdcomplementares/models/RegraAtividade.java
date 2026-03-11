package com.pi.apigenatvdcomplementares.models;

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
@Table(name = "regra_atividade")
@Getter
@Setter
@NoArgsConstructor
public class RegraAtividade extends Auditable {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "area", nullable = false, length = 255)
    private String area;

    @Column(name = "limite_horas", nullable = false)
    private Integer limiteHoras;

    @Column(name = "exige_comprovante", nullable = false)
    private Boolean exigeComprovante;
}