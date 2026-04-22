package com.deliverx.auth.controller;

import com.deliverx.auth.config.JwtUtils;
import com.deliverx.auth.dto.LoginRequest;
import com.deliverx.auth.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    /**
     * @POST /auth/login
     * @Body: { "email": "...", "password": "..." }
     * @Returns: { "accessToken": "...", "tokenType": "Bearer", "expiresIn": 86400 }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest) {

        // Spring Security проверяет email + хэшированный пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new LoginResponse(
                jwt,
                "Bearer",
                jwtUtils.getJwtExpirationMs() / 1000   // перевод в секунды: 86400 сек = 1 день
        ));
    }
}
