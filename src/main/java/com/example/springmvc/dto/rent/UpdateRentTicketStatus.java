package com.example.springmvc.dto.rent;

import lombok.Data;

@Data
public class UpdateRentTicketStatus {
    private String status;
    private String reason;
}