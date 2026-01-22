package com.example.springmvc.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChemicalResponse {
    private Integer itemId;
    private String itemCode;
    private String name;
    private String categoryType;
    private String unit;
    private BigDecimal currentQuantity;
    private BigDecimal lockedQuantity;
    private Integer yearInUse;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // Chemical specific
    private String formula;
    private String supplier;
    private String packaging;
    private String storageLocation;
    private BigDecimal originalPrice;
}