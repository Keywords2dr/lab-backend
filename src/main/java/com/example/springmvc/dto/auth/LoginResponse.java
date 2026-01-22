package com.example.springmvc.dto.auth;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private String role;
}