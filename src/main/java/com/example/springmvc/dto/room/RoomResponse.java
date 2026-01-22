package com.example.springmvc.dto.room;

import lombok.Data;

@Data
public class RoomResponse {
    private Integer roomId;
    private String roomName;
    private String type;
    private Integer capacity;
    private Integer floor;
    private String description;
}