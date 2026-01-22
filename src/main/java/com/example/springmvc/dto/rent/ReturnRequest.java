package com.example.springmvc.dto.rent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ReturnRequest {
    @NotNull(message = "ID phiếu mượn không được để trống")
    private Integer ticketId;

    private String generalNote;

    @Valid
    @NotNull(message = "Danh sách trả không được để trống")
    private List<ReturnItemDetailDto> items;

    @Data
    public static class ReturnItemDetailDto {
        @NotNull(message = "ID vật tư không được để trống")
        private Integer itemId;

        @PositiveOrZero(message = "Số lượng trả không được âm")
        private BigDecimal quantityReturned;

        @NotNull(message = "Tình trạng vật tư không được để trống")
        private String condition;

        private String note;
    }
}