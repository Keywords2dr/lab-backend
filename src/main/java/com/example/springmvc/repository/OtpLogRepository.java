package com.example.springmvc.repository;

import com.example.springmvc.entity.OtpLog;
import com.example.springmvc.entity.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OtpLogRepository extends JpaRepository<OtpLog, Integer> {

    // Tìm mã OTP mới nhất còn hiệu lực theo User và Loại
    Optional<OtpLog> findFirstByUser_UserIdAndOtpTypeAndIsUsedFalseOrderByCreatedAtDesc(
            Integer userId, OtpType type);

    // Chống spam: Tìm mã gần nhất để kiểm tra thời gian 60s
    Optional<OtpLog> findFirstByUser_UserIdAndOtpTypeOrderByCreatedAtDesc(Integer userId, OtpType type);

    // Vô hiệu hóa mã cũ
    List<OtpLog> findByUser_UserIdAndOtpTypeAndIsUsedFalse(Integer userId, OtpType type);
}