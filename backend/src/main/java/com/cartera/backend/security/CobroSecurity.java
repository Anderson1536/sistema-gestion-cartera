
package com.cartera.backend.security;

import com.cartera.backend.repository.CobroRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("cobroSecurity")
public class CobroSecurity {

    private final CobroRepository cobroRepository;

    public CobroSecurity(CobroRepository cobroRepository) {

        this.cobroRepository = cobroRepository;
    }

    public boolean esUsuarioPropio(
            Long usuarioId,
            Authentication authentication) {

        if (authentication instanceof JwtAuthenticationToken jwt) {

            Number id = jwt.getToken().getClaim("usuarioId");

            return id != null
                    && usuarioId != null
                    && usuarioId.equals(id.longValue());
        }

        return false;
    }

    @Transactional(readOnly = true)
    public boolean esCobroPropio(
            Long cobroId,
            Authentication authentication) {

        return cobroRepository.findById(cobroId)
                .map(cobro -> esUsuarioPropio(
                        cobro.getUsuario().getId(),
                        authentication
                ))
                .orElse(false);
    }
}

