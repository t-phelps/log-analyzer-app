package com.loganalyzer.backend.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String tokenType = "Bearer ";

    public AuthResponseDTO(String token, String tokenType) {
        this.token = token;
    }
}
