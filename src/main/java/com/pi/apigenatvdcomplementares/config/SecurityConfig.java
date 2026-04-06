package com.pi.apigenatvdcomplementares.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pi.apigenatvdcomplementares.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
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
                                                // Rotas Públicas (Auth e Documentação)
                                                .requestMatchers(
                                                                "/api/auth/login",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // Gestão de Usuários
                                                .requestMatchers(HttpMethod.POST, "/usuarios", "/usuarios/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.GET, "/usuarios/**").hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/usuarios/**")
                                                .hasRole("SUPER_ADMIN")

                                                // Gestão de Alunos
                                                .requestMatchers("/alunos/**").hasAnyRole("SUPER_ADMIN", "COORDENADOR")

                                                // Gestão de Cursos (Ajustado para capturar a raiz /cursos)
                                                .requestMatchers(HttpMethod.GET, "/cursos", "/cursos/**")
                                                .authenticated()
                                                .requestMatchers(HttpMethod.POST, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.PUT, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")
                                                .requestMatchers(HttpMethod.DELETE, "/cursos", "/cursos/**")
                                                .hasRole("SUPER_ADMIN")

                                                // Local:
                                                // src/main/java/com/pi/apigenatvdcomplementares/config/SecurityConfig.java

                                                // Local:
                                                // src/main/java/com/pi/apigenatvdcomplementares/config/SecurityConfig.java

                                                // Use esta sintaxe para garantir que tanto "/turmas" quanto
                                                // "/turmas/qualquer-coisa" sejam capturados
                                                .requestMatchers(HttpMethod.POST, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.PUT, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.DELETE, "/turmas", "/turmas/**")
                                                .hasAnyRole("SUPER_ADMIN", "COORDENADOR")
                                                .requestMatchers(HttpMethod.GET, "/turmas", "/turmas/**")
                                                .authenticated()
                                                // Gestão de Coordenadores (Ajuste para a tela AdminCoordinators)
                                                .requestMatchers("/coordenadores-cursos/**").hasRole("SUPER_ADMIN")

                                                .anyRequest().authenticated())
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
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