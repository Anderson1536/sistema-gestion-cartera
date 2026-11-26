package com.cartera.backend.dto.response;

public record LoginResponse(

        String token,
        String tipoToken,
        long expiraEn,
        Long usuarioId,
        String nombre,
        String correo,
        String rol

) {}
