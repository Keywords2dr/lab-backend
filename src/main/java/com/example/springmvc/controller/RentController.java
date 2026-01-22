package com.example.springmvc.controller;

import com.example.springmvc.dto.rent.*;
import com.example.springmvc.service.RentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rent")
@RequiredArgsConstructor
public class RentController {

    private final RentService rentService;

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createRentRequest(@Valid @RequestBody RentRequest request) {
        rentService.createRentRequest(request);
        return ResponseEntity.ok(Map.of("message", "Gửi yêu cầu thành công! Vui lòng chờ Admin duyệt."));
    }

    @PostMapping("/review/{id}/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> reviewTicket(
            @PathVariable("id") Integer id,
            @PathVariable("status") String status
    ) {
        rentService.reviewRentTicket(id, status);
        String msg = "APPROVED".equalsIgnoreCase(status) ? "Đã DUYỆT phiếu mượn!" : "Đã TỪ CHỐI phiếu mượn!";
        return ResponseEntity.ok(Map.of("message", msg));
    }

    @PostMapping("/return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> returnTicket(@Valid @RequestBody ReturnRequest request) {
        rentService.returnTicket(request);
        return ResponseEntity.ok(Map.of(
                "message", "Xác nhận trả vật tư/phòng thành công!",
                "ticketId", request.getTicketId()
        ));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<RentListResponse>> getAllTickets(
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(rentService.getAllTickets(status));
    }

    @GetMapping("/monitor-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getMonitorAllTickets(
            @RequestParam(value = "status", required = false) String status
    ) {
        return ResponseEntity.ok(rentService.getAllTickets(status));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getHistoryByUser(@PathVariable("userId") Integer userId) {
        return ResponseEntity.ok(rentService.getRentHistoryByUserId(userId));
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RentListResponse>> getMyHistory() {
        return ResponseEntity.ok(rentService.getMyRentHistory());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RentDetailResponse> getTicketDetail(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(rentService.getTicketDetail(id));
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<RentListResponse>> getRecentActivity() {
        return ResponseEntity.ok(rentService.getRecentActivity());
    }
}