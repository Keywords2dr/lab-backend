package com.example.springmvc.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Vui lòng nhập mật khẩu cũ")
    private String oldPassword;

    @Size(min = 6, message = "Mật khẩu mới phải có tối thiểu 6 ký tự")
    private String newPassword;

    @NotBlank(message = "Vui lòng nhập lại mật khẩu mới")
    private String confirmPassword;

    @Pattern(regexp = "^$|^\\d{6}$", message = "Mã OTP phải bao gồm 6 chữ số")
    private String otpCode;
}