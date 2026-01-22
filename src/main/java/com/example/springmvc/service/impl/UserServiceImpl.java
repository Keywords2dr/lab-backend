package com.example.springmvc.service.impl;

import com.example.springmvc.dto.auth.ChangePasswordRequest;
import com.example.springmvc.dto.user.CreateUserRequest;
import com.example.springmvc.dto.user.UpdateProfileRequest;
import com.example.springmvc.dto.user.UserProfileResponse;
import com.example.springmvc.dto.user.UserResponse;
import com.example.springmvc.entity.*;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.mapper.UserMapper;
import com.example.springmvc.repository.UserRepository;
import com.example.springmvc.repository.UserSecuritySettingsRepository;
import com.example.springmvc.service.OtpService;
import com.example.springmvc.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSecuritySettingsRepository securityRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Value("${file.upload-dir}")
    private String uploadDirConfig;

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = getAuthenticatedUser();
        Profile profile = user.getProfile();

        if (StringUtils.hasText(request.getFullName())) profile.setFullName(request.getFullName().trim());
        if (StringUtils.hasText(request.getPhoneNumber())) profile.setPhoneNumber(request.getPhoneNumber().trim());
        if (StringUtils.hasText(request.getEmail())) profile.setEmail(request.getEmail().trim());
        if (StringUtils.hasText(request.getAvatar())) updateAvatar(profile, request.getAvatar());

        User savedUser = userRepository.save(user);
        return userMapper.toProfileDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser() {
        return userMapper.toProfileDto(getAuthenticatedUser());
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsernameWithProfile(auth.getName());
        if (user == null) throw new BusinessException("Không tìm thấy người dùng!");
        if (user.getProfile() == null) {
            Profile p = new Profile();
            p.setUser(user);
            user.setProfile(p);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("Không tìm thấy user"));
        user.setIsActive(isActive);
        return mapToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId, String currentUsername) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("Không tìm thấy user"));
        if (user.getUsername().equals(currentUsername)) throw new BusinessException("Không thể tự xóa chính mình!");
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Tài khoản đã tồn tại!");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());

        Profile profile = new Profile();
        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
        profile.setUser(user);
        user.setProfile(profile);

        return mapToUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void sendOtp() {
        User user = getAuthenticatedUser();
        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId()).orElse(null);
        if (settings == null || !Boolean.TRUE.equals(settings.getIsTwoFaEnabled())) {
            throw new BusinessException("Tài khoản chưa bật bảo mật 2 lớp!");
        }
        otpService.generateAndSendOtp(user, settings.getSecurityEmail(), OtpType.CHANGE_PASSWORD);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác!");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Mật khẩu xác nhận không khớp!");
        }
        UserSecuritySettings settings = securityRepository.findByUser_UserId(user.getUserId()).orElse(null);
        if (settings != null && Boolean.TRUE.equals(settings.getIsTwoFaEnabled())) {
            if (!StringUtils.hasText(request.getOtpCode())) {
                throw new BusinessException("Vui lòng nhập mã OTP!");
            }
            otpService.verifyOtp(user, request.getOtpCode(), settings.getSecurityEmail(), OtpType.CHANGE_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private void updateAvatar(Profile profile, String base64Data) {
        if (!base64Data.startsWith("data:image")) return;
        try {
            Path rootLocation = Paths.get(uploadDirConfig).resolve("avatars");
            if (!Files.exists(rootLocation)) Files.createDirectories(rootLocation);
            String[] parts = base64Data.split(",");
            String extension = parts[0].contains("jpeg") || parts[0].contains("jpg") ? ".jpg" : ".png";
            byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
            String newFileName = UUID.randomUUID().toString() + extension;
            Files.write(rootLocation.resolve(newFileName), imageBytes);
            profile.setAvatar(newFileName);
        } catch (IOException e) {
            throw new BusinessException("Lỗi hệ thống khi lưu ảnh đại diện.");
        }
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setIsActive(user.getIsActive());
        dto.setCreatedAt(user.getCreatedAt());
        if (user.getProfile() != null) {
            dto.setFullName(user.getProfile().getFullName());
            dto.setEmail(user.getProfile().getEmail());
            dto.setPhoneNumber(user.getProfile().getPhoneNumber());
        }
        return dto;
    }

    @Override public UserResponse getUserById(Integer userId) { return null; }
}