package com.pi.apigenatvdcomplementares.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @Column(name = "area", nullable = false, length = 255)
    private String area;

    @Column(name = "limite_horas", nullable = false)
    private Integer limiteHoras;

    @Column(name = "exige_comprovante", nullable = false)
    private Boolean exigeComprovante;

    @OneToMany(mappedBy = "regraAtividade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRegraAtividade> itens = new ArrayList<>();
}