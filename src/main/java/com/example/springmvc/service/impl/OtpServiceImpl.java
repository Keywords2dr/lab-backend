package com.example.springmvc.service.impl;

import com.example.springmvc.entity.*;
import com.example.springmvc.repository.OtpLogRepository;
import com.example.springmvc.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired private OtpLogRepository otpLogRepository;
    @Autowired private NotificationService notificationService;

    @Autowired private TemplateEngine templateEngine;

    @Autowired
    @Lazy
    private OtpService selfProxy;

    private final SecureRandom secureRandom = new SecureRandom();
    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    @Transactional
    public void generateAndSendOtp(User user, OtpType type) {
        generateAndSendOtp(user, user.getProfile().getEmail(), type);
    }

    @Override
    @Transactional
    public void generateAndSendOtp(User user, String targetEmail, OtpType type) {
        otpLogRepository.findFirstByUser_UserIdAndOtpTypeOrderByCreatedAtDesc(user.getUserId(), type)
                .ifPresent(last -> {
                    if (ChronoUnit.SECONDS.between(last.getCreatedAt(), LocalDateTime.now()) < 60)
                        throw new RuntimeException("Vui lòng đợi 60s trước khi yêu cầu mã mới.");
                });

        otpLogRepository.findByUser_UserIdAndOtpTypeAndIsUsedFalse(user.getUserId(), type)
                .forEach(o -> o.setIsUsed(true));

        String code = String.format("%06d", secureRandom.nextInt(1000000));

        OtpLog log = new OtpLog();
        log.setUser(user);
        log.setOtpCode(code);
        log.setOtpType(type);
        log.setSentTo(targetEmail);
        log.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        log.setFailedAttempts(0);
        otpLogRepository.save(log);

        Context context = new Context();
        context.setVariable("otpCode", code);
        context.setVariable("otpType", type.toString());

        String emailContent = templateEngine.process("otp-email", context);

        notificationService.sendEmail(targetEmail, "Xác thực " + type, emailContent);
    }

    @Override
    @Transactional
    public void verifyOtp(User user, String otpCode, OtpType type) {
        verifyOtp(user, otpCode, null, type);
    }

    @Override
    @Transactional
    public void verifyOtp(User user, String otpCode, String targetEmail, OtpType type) {
        if (!StringUtils.hasText(otpCode)) throw new RuntimeException("Vui lòng nhập mã OTP!");

        OtpLog otpLog = otpLogRepository.findFirstByUser_UserIdAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
                        user.getUserId(), type)
                .orElseThrow(() -> new RuntimeException("Mã OTP không hợp lệ hoặc đã hết hạn."));

        if (otpLog.getFailedAttempts() >= MAX_FAILED_ATTEMPTS) {
            selfProxy.forceInvalidateOtp(otpLog.getLogId());
            throw new RuntimeException("Bạn đã nhập sai quá " + MAX_FAILED_ATTEMPTS + " lần. Mã đã bị vô hiệu hóa.");
        }

        if (!otpLog.getOtpCode().equals(otpCode)) {
            int currentFailed = selfProxy.incrementFailedAttempts(otpLog.getLogId());

            int remaining = MAX_FAILED_ATTEMPTS - currentFailed;
            if (remaining <= 0) {
                selfProxy.forceInvalidateOtp(otpLog.getLogId());
                throw new RuntimeException("Bạn đã nhập sai quá giới hạn. Vui lòng yêu cầu mã mới.");
            }
            throw new RuntimeException("Mã OTP không chính xác. Bạn còn " + remaining + " lần thử.");
        }

        if (otpLog.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Mã OTP đã hết hạn.");
        }

        if (targetEmail != null && !targetEmail.equals(otpLog.getSentTo())) {
            throw new RuntimeException("Thông tin xác thực không khớp.");
        }

        otpLog.setIsUsed(true);
        otpLogRepository.save(otpLog);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int incrementFailedAttempts(Integer logId) {
        OtpLog log = otpLogRepository.findById(logId).orElseThrow();
        int newCount = log.getFailedAttempts() + 1;
        log.setFailedAttempts(newCount);
        otpLogRepository.saveAndFlush(log);
        return newCount;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void forceInvalidateOtp(Integer logId) {
        OtpLog log = otpLogRepository.findById(logId).orElseThrow();
        log.setIsUsed(true);
        otpLogRepository.saveAndFlush(log);
    }
}