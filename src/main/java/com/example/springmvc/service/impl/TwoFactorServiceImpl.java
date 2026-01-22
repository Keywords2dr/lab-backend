package com.example.springmvc.service.impl;

import com.example.springmvc.dto.auth.Enable2FaRequest;
import com.example.springmvc.dto.user.SecuritySettingsResponse;
import com.example.springmvc.dto.auth.VerifyOtpRequest;
import com.example.springmvc.entity.OtpType;
import com.example.springmvc.entity.User;
import com.example.springmvc.entity.UserSecuritySettings;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.repository.UserSecuritySettingsRepository;
import com.example.springmvc.service.OtpService;
import com.example.springmvc.service.TwoFactorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {

    @Autowired private UserSecuritySettingsRepository securityRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private OtpService otpService;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsernameWithProfile(username);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng!");
        return user;
    }

    @Override
    public SecuritySettingsResponse getSecuritySettings() {
        User user = getCurrentUser();
        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElse(new UserSecuritySettings());

        String email = StringUtils.hasText(settings.getSecurityEmail())
                ? settings.getSecurityEmail()
                : (user.getProfile() != null ? user.getProfile().getEmail() : "");

        return new SecuritySettingsResponse(
                Boolean.TRUE.equals(settings.getIsTwoFaEnabled()),
                maskEmail(email)
        );
    }

    @Override
    @Transactional
    public void sendSetupOtp(Enable2FaRequest request) {
        User user = getCurrentUser();

        otpService.generateAndSendOtp(user, request.getEmail(), OtpType.ENABLE_2FA);
    }

    @Override
    @Transactional
    public void verifyAndEnable2Fa(VerifyOtpRequest request) {
        User user = getCurrentUser();

        otpService.verifyOtp(user, request.getOtpCode(), request.getEmail(), OtpType.ENABLE_2FA);

        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElse(new UserSecuritySettings());
        if (settings.getUser() == null) settings.setUser(user);

        settings.setIsTwoFaEnabled(true);
        settings.setSecurityEmail(request.getEmail());
        securityRepository.save(settings);
    }

    @Override
    @Transactional
    public void requestChangeEmail(String newEmail) {
        if (!StringUtils.hasText(newEmail)) throw new RuntimeException("Email mới không được để trống!");
        User user = getCurrentUser();

        otpService.generateAndSendOtp(user, newEmail, OtpType.CHANGE_2FA_EMAIL);
    }

    @Override
    @Transactional
    public void confirmChangeEmail(String newEmail, String otp) {
        User user = getCurrentUser();

        otpService.verifyOtp(user, otp, newEmail, OtpType.CHANGE_2FA_EMAIL);

        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng chưa thiết lập bảo mật!"));

        settings.setSecurityEmail(newEmail);
        settings.setIsTwoFaEnabled(true);
        securityRepository.save(settings);
    }

    @Override
    @Transactional
    public void disable2Fa() {
        User user = getCurrentUser();
        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng chưa thiết lập bảo mật!"));

        settings.setIsTwoFaEnabled(false);
        securityRepository.save(settings);
    }

    private String maskEmail(String email) {
        if (!StringUtils.hasText(email)) return "Chưa cập nhật";
        int atIndex = email.indexOf("@");
        if (atIndex <= 3) return email;
        return email.substring(0, 3) + "***" + email.substring(atIndex);
    }
}