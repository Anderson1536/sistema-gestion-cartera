package com.cartera.backend.security;

import com.cartera.backend.entity.Usuario;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final long expiracion;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.expiration}") long expiracion) {

        this.jwtEncoder = jwtEncoder;
        this.expiracion = expiracion;
    }

    public String generarToken(Usuario usuario) {

        Instant ahora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("sistema-gestion-cartera")
                .issuedAt(ahora)
                .expiresAt(ahora.plusMillis(expiracion))
                .subject(usuario.getCorreo())
                .claim("usuarioId", usuario.getId())
                .claim("rol", usuario.getRol().getNombre())
                .build();

        JwsHeader header = JwsHeader
                .with(MacAlgorithm.HS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public long obtenerExpiracion() {

        return expiracion / 1000;
    }
}
