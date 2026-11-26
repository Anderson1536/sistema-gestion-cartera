
package com.cartera.backend.config;

import com.cartera.backend.repository.UsuarioRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        @Value("${app.cors.allowed-origins}")
        private String allowedOrigins;

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        UsuarioRepository usuarioRepository,
                        TransactionTemplate transactionTemplate) throws Exception {

                http
                                .csrf(csrf -> csrf.disable())

                                .cors(cors -> cors.configurationSource(
                                                corsConfigurationSource()))

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                // Inicio de sesión y documentación
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/auth/login")
                                                .permitAll()

                                                .requestMatchers(
                                                                "/swagger-ui.html",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**")
                                                .permitAll()

                                                // Gestión de roles y usuarios
                                                .requestMatchers(
                                                                "/api/roles/**",
                                                                "/api/usuarios/**")
                                                .hasRole("ADMINISTRADOR")

                                                // Consulta de clientes
                                                .requestMatchers(
                                                                HttpMethod.GET,
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "ANALISTA",
                                                                "REPRESENTANTE")

                                                // Gestión de clientes
                                                .requestMatchers(
                                                                "/api/clientes/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "ANALISTA")

                                                // Validación y rechazo de cobros
                                                .requestMatchers(
                                                                HttpMethod.PATCH,
                                                                "/api/cobros/*/estado")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "ANALISTA")

                                                // Registro de cobros
                                                .requestMatchers(
                                                                HttpMethod.POST,
                                                                "/api/cobros")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "REPRESENTANTE")

                                                // Consulta y actualización de cobros
                                                .requestMatchers(
                                                                "/api/cobros/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "ANALISTA",
                                                                "REPRESENTANTE")

                                                // Dashboard
                                                .requestMatchers(
                                                                "/api/dashboard/**")
                                                .hasAnyRole(
                                                                "ADMINISTRADOR",
                                                                "ANALISTA")

                                                .anyRequest().denyAll())

                                .oauth2ResourceServer(
                                                oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(token ->

                                                transactionTemplate.execute(status -> {

                                                        var usuario = usuarioRepository
                                                                        .findByCorreo(token.getSubject())
                                                                        .orElseThrow(() -> tokenInvalido());

                                                        if (!Boolean.TRUE.equals(
                                                                        usuario.getActivo())
                                                                        || !usuario.getId().equals(
                                                                                        token.getClaimAsString(
                                                                                                        "usuarioId") == null
                                                                                                                        ? null
                                                                                                                        : Long.valueOf(token
                                                                                                                                        .getClaimAsString(
                                                                                                                                                        "usuarioId")))) {

                                                                throw tokenInvalido();
                                                        }

                                                        String rol = usuario.getRol().getNombre();

                                                        return new JwtAuthenticationToken(
                                                                        token,
                                                                        List.of(
                                                                                        new SimpleGrantedAuthority(
                                                                                                        "ROLE_" + rol)));
                                                }))));

                return http.build();
        }

        private OAuth2AuthenticationException tokenInvalido() {

                return new OAuth2AuthenticationException(
                                new OAuth2Error(
                                                "invalid_token",
                                                "Usuario inactivo o no válido",
                                                null));
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {

                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOrigins(
                                List.of(allowedOrigins.split("\\s*,\\s*")));

                config.setAllowedMethods(
                                List.of("GET", "POST", "PUT", "PATCH", "OPTIONS"));

                config.setAllowedHeaders(
                                List.of("Authorization", "Content-Type"));

                config.setAllowCredentials(false);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

                source.registerCorsConfiguration("/**", config);

                return source;
        }
}
