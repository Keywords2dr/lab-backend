package com.example.springmvc.service.impl;

import com.example.springmvc.dto.room.RoomResponse;
import com.example.springmvc.entity.Room;
import com.example.springmvc.mapper.RoomMapper;
import com.example.springmvc.repository.RoomRepository;
import com.example.springmvc.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    public List<RoomResponse> getBookingRooms() {
        List<Room> rooms = roomRepository.findByTypeIn(Arrays.asList("LAB"));

        return rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getActiveRooms() {
        List<Room> rooms = roomRepository.findByIsActiveTrue();

        return rooms.stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }
}