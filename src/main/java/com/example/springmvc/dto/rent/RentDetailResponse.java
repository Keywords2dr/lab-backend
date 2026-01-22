package com.example.springmvc.dto.rent;

import com.example.springmvc.entity.RentTicketDetail;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RentDetailResponse {
    private Integer ticketId;
    private String roomName;
    private LocalDateTime borrowDate;
    private LocalDateTime expectedReturnDate;
    private String status;
    private LocalDateTime createdDate;
    private String borrowerName;
    private String borrowerUsername;
    private List<RentTicketDetail> details;
}