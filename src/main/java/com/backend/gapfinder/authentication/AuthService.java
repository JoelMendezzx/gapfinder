package com.backend.gapfinder.authentication;

import com.backend.gapfinder.entities.tokenrefresh.RefreshTokenEntity;
import com.backend.gapfinder.entities.tokenrefresh.RefreshTokenService;
import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.entities.user.UserRepository;
import com.backend.gapfinder.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

// Lógica de registro, login, refresh y logout
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Crea un usuario nuevo y le devuelve sus tokens de sesión
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo");
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setProgram(request.program());
        user.setSemester(request.semester());
        user.setActivityEffortPreference(request.activityEffortPreference());
        user.setAvatarUrl(null);
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    // Valida credenciales y devuelve los tokens de sesión
    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.email().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Correo o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Correo o contraseña incorrectos");
        }

        return buildAuthResponse(user);
    }

    // Genera un access token nuevo a partir de un refresh token válido
    public AuthResponse refresh(RefreshRequest request) {
        RefreshTokenEntity refreshToken = refreshTokenService.verifyAndGet(request.refreshToken());
        UserEntity user = refreshToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user.getEmail());

        return new AuthResponse(newAccessToken, refreshToken.getToken(), user.getEmail(), user.getName());
    }

    // Cierra sesión, eliminando el refresh token del usuario
    public void logout(UserEntity user) {
        refreshTokenService.revokeByUserId(user.getId());
    }

    // Arma la respuesta de auth generando ambos tokens (access + refresh)
    private AuthResponse buildAuthResponse(UserEntity user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail());
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), user.getEmail(), user.getName());
    }
}