package com.example.springmvc.service.impl;

import com.example.springmvc.service.NotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired private JavaMailSender emailSender;
    @Autowired private Environment env;

    @Override
    @Async("mailExecutor")
    public void sendEmail(String to, String subject, String content) {
        try {
            log.info("📧 Đang chuẩn bị gửi mail tới: {}", to);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String fromEmail = env.getProperty("mail.from");

            helper.setFrom(fromEmail, "He Thong QNU");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            emailSender.send(message);
            log.info("✅ Gửi mail thành công tới: {}", to);

        } catch (Exception e) {
            log.error("❌ Lỗi nghiêm trọng khi gửi mail tới {}: {}", to, e.getMessage());
        }
    }
}