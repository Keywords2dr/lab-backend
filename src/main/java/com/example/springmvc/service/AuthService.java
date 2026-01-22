package com.example.springmvc.service;

import com.example.springmvc.dto.auth.LoginRequest;
import com.example.springmvc.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}