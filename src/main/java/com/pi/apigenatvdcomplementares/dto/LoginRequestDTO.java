package com.pi.apigenatvdcomplementares.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    
    @NotBlank(message = "Email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "senha é obrigatória")
    private String senha;
}
