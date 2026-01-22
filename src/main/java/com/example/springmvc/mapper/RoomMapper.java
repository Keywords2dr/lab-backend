package com.example.springmvc.mapper;

import com.example.springmvc.dto.room.RoomResponse;
import com.example.springmvc.entity.Room;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    RoomResponse toDto(Room room);
}