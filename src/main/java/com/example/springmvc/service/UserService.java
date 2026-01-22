package com.example.springmvc.service;

import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.CreateUserRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;
import com.example.springmvc.dto.user.UserResponse;
import java.util.List;

public interface UserService {
    UserProfileResponse getCurrentUser();
    UserProfileResponse updateProfile(UpdateProfileRequest request);
    void sendOtp();
    void changePassword(ChangePasswordRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Integer userId);
    UserResponse updateUserStatus(Integer userId, Boolean isActive);
    void deleteUser(Integer userId, String currentUsername);
    UserResponse createUser(CreateUserRequest request);
}