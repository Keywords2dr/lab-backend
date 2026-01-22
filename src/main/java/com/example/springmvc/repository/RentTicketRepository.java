package com.example.springmvc.repository;

import com.example.springmvc.entity.RentTicket;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RentTicketRepository extends JpaRepository<RentTicket, Integer> {

    @EntityGraph(attributePaths = {"user", "user.profile", "room"})
    List<RentTicket> findTop5ByOrderByCreatedDateDesc();

    @EntityGraph(attributePaths = {"user", "user.profile", "room"})
    List<RentTicket> findByStatusOrderByCreatedDateDesc(String status);

    @EntityGraph(attributePaths = {"user", "user.profile", "room"})
    List<RentTicket> findByUser_UserIdOrderByCreatedDateDesc(Integer userId);

    @EntityGraph(attributePaths = {"user", "user.profile", "room"})
    List<RentTicket> findAllByOrderByCreatedDateDesc();

    @Query("SELECT COUNT(t) FROM RentTicket t WHERE t.room.roomId = :roomId " +
            "AND t.status IN ('PENDING', 'APPROVED') " +
            "AND t.borrowDate < :reqEnd AND t.expectedReturnDate > :reqStart")
    long countActiveBookings(@Param("roomId") Integer roomId,
                             @Param("reqStart") LocalDateTime reqStart,
                             @Param("reqEnd") LocalDateTime reqEnd);
}