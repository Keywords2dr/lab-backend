package com.example.springmvc.dto.rent;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RentListResponse {
    private Integer ticketId;
    private String roomName;
    private String borrowerName;
    private LocalDateTime borrowDate;
    private LocalDateTime expectedReturnDate;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;

    private String itemSummary;
}