package com.example.springmvc.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssetResponse {
    private Integer itemId;
    private String itemCode;
    private String name;
    private String categoryType;
    private String unit;
    private Integer yearInUse;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private BigDecimal residualValue;
    private Integer accountingQuantity;
    private Integer inventoryQuantity;
    private String statusDetail;
    private String supplier;
    private String storageLocation;
    private BigDecimal originalPrice;
}