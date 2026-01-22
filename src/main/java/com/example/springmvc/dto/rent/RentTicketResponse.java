package com.example.springmvc.dto.rent;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RentTicketResponse {
    private Integer ticketId;
    private Integer userId;
    private String username;
    private String fullName;
    private Integer roomId;
    private String roomName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expectedReturnDate;

    private String status;
    private List<RentTicketDetailDTO> details;

    @Data
    public static class RentTicketDetailDTO {
        private Integer detailId;
        private Integer itemId;
        private String itemName;
        private String itemCode;
        private String categoryType;
        private java.math.BigDecimal quantity;
        private String unit;
        private String returnStatus;
    }
}