package com.example.springmvc.dto.rent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealTimeRentInfo {
    private String message;      // Nội dung thông báo (Ví dụ: "Phòng Lab 1 vừa được đặt")
    private String roomName;     // Tên phòng bị thay đổi trạng thái
    private String status;       // Trạng thái mới (PENDING, APPROVED, REJECTED)
    private String username;     // Người thực hiện hành động
    private String createdTime;  // Thời gian tạo (dạng chuỗi cho dễ hiển thị)
}