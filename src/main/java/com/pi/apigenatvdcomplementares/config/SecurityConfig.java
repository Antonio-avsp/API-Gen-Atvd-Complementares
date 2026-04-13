package com.pi.apigenatvdcomplementares.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pi.apigenatvdcomplementares.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final UserDetailsService userDetailsService;
        private final PasswordEncoder passwordEncoder;

        public SecurityConfig(
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        UserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.userDetailsService = userDetailsService;
                this.passwordEncoder = passwordEncoder;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http
                                .cors(cors -> {
                                })
                                .csrf(csrf -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth

                                                // ── Rotas públicas ────────────────────────────────
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/auth/recuperar-senha",
                                                                "/auth/validar-codigo",
                                                                "/auth/redefinir-senha",
                                                                "/auth/password/solicitar",
                                                                "/auth/password/validar",
                                                                "/auth/password/redefinir",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // ── /usuarios/me: qualquer usuário autenticado ────
                                                // Permite que o aluno consulte os próprios dados
                                                // (necessário para resolver o alunoId no front-end)
                                                .requestMatchers(HttpMethod.GET, "/usuarios/me")
                                                .authenticated()

                                                // ── Gestão de Usuários (SUPER_ADMIN) ─────────────
                                                .requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/usuarios/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/usuarios/**")
                                                .hasRole("SUPER_ADMIN")

                                                // ── Gestão de Alunos ──────────────────────────────
                                                // /alunos/me e /alunos/me/cursos: aluno consulta os próprios dados
                                                .requestMatchers(HttpMethod.GET, "/alunos/me", "/alunos/me/cursos")
                                                .authenticated()
                                                // demais endpoints de aluno: apenas admin e coordenador
                                                .requestMatchers("/alunos/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")

                                                // ── Gestão de Cursos ──────────────────────────────
                                                .requestMatchers(HttpMethod.GET, "/cursos", "/cursos/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")

                                                // ── Gestão de Turmas ──────────────────────────────
                                                .requestMatchers(HttpMethod.POST, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.PUT, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.DELETE, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.GET, "/turmas", "/turmas/**")
                                                .authenticated()

                                                // ── Gestão de Regras ──────────────────────────────
                                                // Aluno pode consultar regras do próprio curso (GET)
                                                .requestMatchers(HttpMethod.GET, "/regras", "/regras/**")
                                                .authenticated()
                                                // Apenas admin e coordenador podem criar/editar/deletar
                                                .requestMatchers(HttpMethod.POST, "/regras", "/regras/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.PUT, "/regras/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.DELETE, "/regras/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")

                                                // ── Coordenadores ─────────────────────────────────
                                                .requestMatchers("/coordenadores-cursos/**")
                                                .hasRole("SUPER_ADMIN")

                                                // ── Submissões ────────────────────────────────────
                                                // Aluno pode criar e listar as próprias submissões
                                                .requestMatchers(HttpMethod.GET, "/submissoes", "/submissoes/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/submissoes", "/submissoes/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/submissoes/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/submissoes/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")

                                                // ── Certificados ──────────────────────────────────
                                                // Aluno pode anexar certificados às próprias submissões
                                                .requestMatchers(HttpMethod.GET, "/certificados", "/certificados/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/certificados", "/certificados/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.PATCH, "/certificados/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.DELETE, "/certificados/**")
                                                .authenticated()

                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                .build();
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}