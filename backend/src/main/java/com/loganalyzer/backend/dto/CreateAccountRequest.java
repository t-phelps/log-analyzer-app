package com.loganalyzer.backend.dto;

public record CreateAccountRequest(String email, String username, String password) {
}
