package com.example.springmvc.service;

public interface NotificationService {
    void sendEmail(String to, String subject, String content);
}