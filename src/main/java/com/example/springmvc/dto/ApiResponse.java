package com.example.springmvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String message;
    private String error;

    public static ApiResponse success(String message) {
        return new ApiResponse(message, null);
    }

    public static ApiResponse error(String error) {
        return new ApiResponse(null, error);
    }
}