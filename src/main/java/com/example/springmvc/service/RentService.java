package com.example.springmvc.service;

import com.example.springmvc.dto.rent.RentDetailResponse;
import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.rent.RentRequest;
import com.example.springmvc.dto.rent.ReturnRequest;
import java.util.List;

public interface RentService {
    void createRentRequest(RentRequest request);
    void reviewRentTicket(Integer ticketId, String status);
    void returnTicket(ReturnRequest request);
    List<RentListResponse> getAllTickets(String status);
    List<RentListResponse> getRentHistoryByUserId(Integer userId);
    List<RentListResponse> getMyRentHistory();
    RentDetailResponse getTicketDetail(Integer ticketId);
    List<RentListResponse> getRecentActivity();
}