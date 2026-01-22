package com.example.springmvc.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "rooms")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "room_name", nullable = false)
    private String roomName;

    private Integer floor;
    private Integer capacity;

    @Column(nullable = false)
    private String type; // LAB, CLASS, WAREHOUSE, OFFICE

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true; // Mặc định là true (đang hoạt động)
}