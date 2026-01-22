package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@PrimaryKeyJoinColumn(name = "item_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Asset extends Item {

    @Column(name = "spec_id", insertable = false, updatable = false)
    private Integer specId;


    @Column(name = "residual_value")
    private BigDecimal residualValue; // Giá trị còn lại (Sau khấu hao)

    @Column(name = "accounting_quantity")
    private Integer accountingQuantity; // Số lượng trên sổ sách kế toán

    @Column(name = "inventory_quantity")
    private Integer inventoryQuantity; // Số lượng kiểm kê thực tế

    @Column(name = "status_detail")
    private String statusDetail; // Chi tiết tình trạng (Mới 100%, Hỏng nhẹ...)
}