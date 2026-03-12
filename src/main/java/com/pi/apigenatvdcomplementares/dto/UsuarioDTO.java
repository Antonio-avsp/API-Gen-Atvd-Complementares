package com.pi.apigenatvdcomplementares.dto;

import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsuarioDTO {

    private String id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;

    public UsuarioDTO(Usuario u) {
        this.id = u.getId();
        this.nome = u.getNome();
        this.email = u.getEmail();
        this.perfil = u.getPerfil();
    }

}
