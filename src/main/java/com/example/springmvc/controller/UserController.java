package com.example.springmvc.controller;

import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;
import com.example.springmvc.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getCurrentProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp() {
        userService.sendOtp();
        return ResponseEntity.ok(Map.of("message", "Mã OTP xác thực đã được gửi tới email của bạn!"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công!"));
    }
}