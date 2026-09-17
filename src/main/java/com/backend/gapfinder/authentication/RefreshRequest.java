package com.backend.gapfinder.authentication;

import jakarta.validation.constraints.NotBlank;

// Body que se envía para pedir un access token nuevo usando el refresh token
public record RefreshRequest(
    @NotBlank(message = "El refresh token es obligatorio")
    String refreshToken
) {}