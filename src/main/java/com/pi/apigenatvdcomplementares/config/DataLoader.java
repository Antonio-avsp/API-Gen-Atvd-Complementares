package com.pi.apigenatvdcomplementares.config;

import com.pi.apigenatvdcomplementares.enums.PerfilUsuario;
import com.pi.apigenatvdcomplementares.models.Usuario;
import com.pi.apigenatvdcomplementares.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            criarUsuarioSeNaoExistir(
                    usuarioRepository,
                    passwordEncoder,
                    "vitorshampo@gmail.com",
                    "Vitor Shampo",
                    "123123",
                    PerfilUsuario.ALUNO
            );

            criarUsuarioSeNaoExistir(
                    usuarioRepository,
                    passwordEncoder,
                    "ameliara@gmail.com",
                    "ameliara",
                    "123123",
                    PerfilUsuario.COORDENADOR
            );

            criarUsuarioSeNaoExistir(
                    usuarioRepository,
                    passwordEncoder,
                    "marcoantonio@gmail.com",
                    "Marco Antonio",
                    "123123",
                    PerfilUsuario.SUPER_ADMIN
            );
        };
    }

    private void criarUsuarioSeNaoExistir(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String nome,
            String senha,
            PerfilUsuario perfil
    ) {
        if (usuarioRepository.findByEmail(email).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setSenha(passwordEncoder.encode(senha));
            usuario.setPerfil(perfil);

            usuarioRepository.save(usuario);

            System.out.println("Usuário criado: " + email + " - " + perfil.name());
        } else {
            System.out.println("Usuário já existe: " + email);
        }
    }
}