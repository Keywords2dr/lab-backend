package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "chemicals")
@PrimaryKeyJoinColumn(name = "item_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Chemical extends Item {

    @Column(name = "spec_id", insertable = false, updatable = false)
    private Integer specId;

    @Column(name = "formula")
    private String formula; // Công thức hóa học

    @Column(name = "packaging")
    private String packaging; // Quy cách đóng gói (VD: Chai 500ml, Can 5L)
}