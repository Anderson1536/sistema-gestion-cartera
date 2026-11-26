package com.cartera.backend.service;

import com.cartera.backend.dto.request.LoginRequest;
import com.cartera.backend.dto.response.LoginResponse;
import com.cartera.backend.entity.Usuario;
import com.cartera.backend.repository.UsuarioRepository;
import com.cartera.backend.security.JwtService;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse iniciarSesion(LoginRequest request) {

        String correo = request.correo()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (request.contrasena()
                .getBytes(StandardCharsets.UTF_8).length > 72) {

            throw new BadCredentialsException(
                    "Credenciales incorrectas"
            );
        }

        Usuario usuario = usuarioRepository
                .findByCorreo(correo)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Credenciales incorrectas"
                        ));

        if (!Boolean.TRUE.equals(usuario.getActivo())
                || !passwordEncoder.matches(
                        request.contrasena(),
                        usuario.getContrasena())) {

            throw new BadCredentialsException(
                    "Credenciales incorrectas"
            );
        }

        String token = jwtService.generarToken(usuario);

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.obtenerExpiracion(),
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }
}
