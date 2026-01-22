package com.example.springmvc.dto.item;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemResponse {
    private Integer itemId;
    private String name;
    private String categoryType;
    private String unit;
    private BigDecimal availableQuantity;
    private Integer yearInUse;
    private String formula;
    private String packaging;
    private String supplier;
}