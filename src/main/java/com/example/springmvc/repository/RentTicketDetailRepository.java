package com.example.springmvc.repository;

import com.example.springmvc.entity.RentTicketDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentTicketDetailRepository extends JpaRepository<RentTicketDetail, Integer> {
    List<RentTicketDetail> findByRentTicket_TicketId(Integer ticketId);
}