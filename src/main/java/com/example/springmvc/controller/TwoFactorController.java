package com.example.springmvc.controller;

import com.example.springmvc.dto.auth.ChangeTwoFaEmailRequest;
import com.example.springmvc.dto.auth.Enable2FaRequest;
import com.example.springmvc.dto.auth.VerifyOtpRequest;
import com.example.springmvc.dto.user.SecuritySettingsResponse;
import com.example.springmvc.service.TwoFactorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {

    @Autowired
    private TwoFactorService twoFactorService;

    @GetMapping("/settings")
    public ResponseEntity<SecuritySettingsResponse> getSecuritySettings() {
        return ResponseEntity.ok(twoFactorService.getSecuritySettings());
    }

    @PostMapping("/setup/request")
    public ResponseEntity<?> setup2FaRequest(@Valid @RequestBody Enable2FaRequest request) {
        twoFactorService.sendSetupOtp(request);
        return ResponseEntity.ok(Map.of("message", "Mã xác thực đã gửi tới: " + request.getEmail()));
    }

    @PostMapping("/setup/verify")
    public ResponseEntity<?> setup2FaVerify(@Valid @RequestBody VerifyOtpRequest request) {
        twoFactorService.verifyAndEnable2Fa(request);
        return ResponseEntity.ok(Map.of("message", "Bật bảo mật 2 lớp thành công!"));
    }

    @PostMapping("/change-email/request")
    public ResponseEntity<?> requestChangeEmail(@Valid @RequestBody ChangeTwoFaEmailRequest request) {
        twoFactorService.requestChangeEmail(request.getNewEmail());
        return ResponseEntity.ok(Map.of("message", "Mã OTP xác thực đã được gửi tới email mới: " + request.getNewEmail()));
    }

    @PostMapping("/change-email/verify")
    public ResponseEntity<?> verifyChangeEmail(@Valid @RequestBody VerifyOtpRequest request) {
        twoFactorService.confirmChangeEmail(request.getEmail(), request.getOtpCode());
        return ResponseEntity.ok(Map.of("message", "Cập nhật email bảo mật thành công!"));
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disable2Fa() {
        twoFactorService.disable2Fa();
        return ResponseEntity.ok(Map.of("message", "Đã tắt tính năng bảo mật 2 lớp."));
    }
}