package com.example.springmvc.service;

import com.example.springmvc.dto.room.RoomResponse;
import java.util.List;

public interface RoomService {
    List<RoomResponse> getBookingRooms();
    List<RoomResponse> getActiveRooms();
}