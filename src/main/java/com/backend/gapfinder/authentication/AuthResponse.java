package com.backend.gapfinder.authentication;

// Respuesta al hacer login o registro exitoso
public record AuthResponse(
    String accessToken,  // JWT de corta duración, se manda en cada petición
    String refreshToken, // token opaco de larga duración, sirve para pedir un accessToken nuevo
    String email,
    String name
) {}