package com.example.springmvc.dto.rent;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RentRequest {
    @NotNull(message = "Vui lòng chọn phòng thí nghiệm")
    private Integer roomId;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu mượn")
    @FutureOrPresent(message = "Ngày mượn không được ở trong quá khứ")
    private LocalDateTime borrowDate;

    @NotNull(message = "Vui lòng chọn ngày dự kiến trả")
    @Future(message = "Ngày trả phải ở trong tương lai")
    private LocalDateTime expectedReturnDate;

    @NotEmpty(message = "Danh sách mượn không được để trống")
    @Valid
    private List<RentItemDto> items;

    @AssertTrue(message = "Ngày trả dự kiến phải sau thời gian bắt đầu mượn!")
    public boolean isValidDateRange() {
        if (borrowDate == null || expectedReturnDate == null) return true;
        return expectedReturnDate.isAfter(borrowDate);
    }

    @Data
    public static class RentItemDto {
        @NotNull(message = "ID món đồ không được để trống")
        private Integer itemId;

        @NotNull(message = "Số lượng không được để trống")
        @Positive(message = "Số lượng mượn phải là số dương lớn hơn 0")
        private BigDecimal quantity;
    }
}