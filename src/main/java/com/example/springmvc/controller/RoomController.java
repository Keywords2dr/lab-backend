package com.example.springmvc.controller;

import com.example.springmvc.dto.room.RoomResponse;
import com.example.springmvc.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/booking-available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getBookingRooms());
    }

    @GetMapping("/active")
    public ResponseEntity<List<RoomResponse>> getActiveRooms() {
        return ResponseEntity.ok(roomService.getActiveRooms());
    }
}