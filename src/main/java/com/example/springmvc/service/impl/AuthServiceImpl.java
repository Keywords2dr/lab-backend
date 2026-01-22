package com.example.springmvc.service.impl;

import com.example.springmvc.constant.RoleConst;
import com.example.springmvc.dto.auth.LoginRequest;
import com.example.springmvc.dto.auth.LoginResponse;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.security.JwtUtils;
import com.example.springmvc.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = new LoginResponse();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();

                String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("");

                if (RoleConst.ADMIN.equals(role) || RoleConst.TEACHER.equals(role)) {
                    String token = jwtUtils.generateToken(request.getUsername(), role);
                    response.setSuccess(true);
                    response.setMessage("Đăng nhập thành công!");
                    response.setToken(token);
                    response.setRole(role);
                } else {
                    throw new BusinessException("Tài khoản của bạn không có quyền truy cập hệ thống!");
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Sai tài khoản hoặc mật khẩu!");
        }
        return response;
    }
}