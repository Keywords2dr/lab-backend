package com.example.springmvc.dto.user;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Integer userId;
    private String username;
    private String role;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String department;
    private String faculty;
    private String avatar;
}