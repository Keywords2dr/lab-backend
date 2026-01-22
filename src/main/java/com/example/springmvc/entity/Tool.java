package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Entity
@Table(name = "tools")
@PrimaryKeyJoinColumn(name = "item_id")
@Data
@EqualsAndHashCode(callSuper = true)
public class Tool extends Item {

    @Column(name = "spec_id", insertable = false, updatable = false)
    private Integer specId;

    @Column(name = "technical_grade")
    private String technicalGrade;

    @Column(name = "actual_amount")
    private BigDecimal actualAmount;

    @Column(name = "liquidation_proposal")
    private Boolean liquidationProposal;

}