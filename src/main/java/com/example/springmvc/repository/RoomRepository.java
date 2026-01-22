package com.example.springmvc.repository;

import com.example.springmvc.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findByTypeIn(List<String> types);
    List<Room> findByIsActiveTrue();
}