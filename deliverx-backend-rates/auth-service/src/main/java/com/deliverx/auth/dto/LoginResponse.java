package com.deliverx.auth.dto;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn
) {}
