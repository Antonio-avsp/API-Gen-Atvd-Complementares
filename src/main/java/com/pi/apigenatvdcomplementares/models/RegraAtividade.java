package com.pi.apigenatvdcomplementares.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "tb_regra_atividade")
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