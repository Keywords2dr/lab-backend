package com.example.springmvc.service;

import com.example.springmvc.entity.User;
import com.example.springmvc.entity.OtpType;

public interface OtpService {
    void generateAndSendOtp(User user, OtpType type);
    void generateAndSendOtp(User user, String targetEmail, OtpType type);
    void verifyOtp(User user, String otpCode, OtpType type);
    void verifyOtp(User user, String otpCode, String targetEmail, OtpType type);

    int incrementFailedAttempts(Integer logId);
    void forceInvalidateOtp(Integer logId);
}