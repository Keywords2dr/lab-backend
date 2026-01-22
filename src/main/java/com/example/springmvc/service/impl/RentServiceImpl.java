package com.example.springmvc.service.impl;

import com.example.springmvc.constant.RentConst;
import com.example.springmvc.dto.rent.RentDetailResponse;
import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.dto.rent.RentRequest;
import com.example.springmvc.dto.rent.ReturnRequest;
import com.example.springmvc.entity.*;
import com.example.springmvc.exception.BusinessException;
import com.example.springmvc.mapper.RentMapper;
import com.example.springmvc.repository.*;
import com.example.springmvc.service.RentService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    private final RentTicketRepository rentTicketRepository;
    private final ItemRepository itemRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RentMapper rentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createRentRequest(RentRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = request.getBorrowDate();
        LocalDateTime end = request.getExpectedReturnDate();

        if (start.isBefore(now)) {
            throw new BusinessException("Thời gian mượn không được nhỏ hơn thời gian hiện tại!");
        }

        if (end.isBefore(start) || end.equals(start)) {
            throw new BusinessException("Thời gian trả phải sau thời gian mượn!");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BusinessException("Phòng không tồn tại!"));

        if (!"LAB".equals(room.getType())) {
            throw new BusinessException("Chỉ có thể đặt Phòng thí nghiệm (LAB).");
        }

        long conflicts = rentTicketRepository.countActiveBookings(request.getRoomId(), start, end);
        if (conflicts > 0) {
            throw new BusinessException("Phòng " + room.getRoomName() + " đã bị đặt kín trong khung giờ này.");
        }

        for (RentRequest.RentItemDto dto : request.getItems()) {
            Item item = itemRepository.findById(dto.getItemId())
                    .orElseThrow(() -> new BusinessException("Vật tư không tồn tại!"));

            if (!(item instanceof Chemical)) {
                if (dto.getQuantity().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                    throw new BusinessException("Lỗi: '" + item.getName() + "' là thiết bị/công cụ, chỉ được nhập số lượng nguyên!");
                }
            }

            BigDecimal current = item.getCurrentQuantity() != null ? item.getCurrentQuantity() : BigDecimal.ZERO;
            BigDecimal locked = item.getLockedQuantity() != null ? item.getLockedQuantity() : BigDecimal.ZERO;
            BigDecimal available = current.subtract(locked);

            if (dto.getQuantity().compareTo(available) > 0) {
                String unit = item.getUnit() == null ? "" : item.getUnit();
                throw new BusinessException("Món '" + item.getName() + "' chỉ còn lại " + available + " " + unit + " trong kho!");
            }

            item.setLockedQuantity(locked.add(dto.getQuantity()));
            itemRepository.save(item);
        }

        RentTicket ticket = new RentTicket();
        ticket.setUser(user);
        ticket.setRoom(room);
        ticket.setBorrowDate(start);
        ticket.setExpectedReturnDate(end);
        ticket.setStatus(RentConst.PENDING);

        RentTicket savedTicket = rentTicketRepository.save(ticket);

        List<RentTicketDetail> details = new ArrayList<>();
        for (RentRequest.RentItemDto dto : request.getItems()) {
            RentTicketDetail detail = new RentTicketDetail();
            detail.setRentTicket(savedTicket);
            detail.setItem(itemRepository.findById(dto.getItemId()).orElseThrow());
            detail.setQuantityBorrowed(dto.getQuantity());
            detail.setReturnStatus(RentConst.NOT_RETURNED);
            details.add(detail);
        }
        savedTicket.setDetails(details);
        rentTicketRepository.save(savedTicket);

        broadcastTicketUpdate(savedTicket);
    }

    @Override
    @Transactional
    public void reviewRentTicket(Integer ticketId, String status) {
        String finalStatus = status.trim().toUpperCase();
        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Phiếu mượn không tồn tại!"));

        if (!RentConst.PENDING.equals(ticket.getStatus())) {
            throw new BusinessException("Chỉ có thể xử lý các phiếu đang ở trạng thái CHỜ!");
        }

        for (RentTicketDetail detail : ticket.getDetails()) {
            Item item = detail.getItem();
            BigDecimal qty = detail.getQuantityBorrowed();
            BigDecimal locked = item.getLockedQuantity() == null ? BigDecimal.ZERO : item.getLockedQuantity();
            BigDecimal current = item.getCurrentQuantity() == null ? BigDecimal.ZERO : item.getCurrentQuantity();

            if (RentConst.APPROVED.equals(finalStatus)) {
                item.setCurrentQuantity(current.subtract(qty));
                item.setLockedQuantity(locked.subtract(qty).max(BigDecimal.ZERO));
            } else if (RentConst.REJECTED.equals(finalStatus)) {
                item.setLockedQuantity(locked.subtract(qty).max(BigDecimal.ZERO));
            }
            itemRepository.save(item);
        }

        ticket.setStatus(finalStatus);
        RentTicket updatedTicket = rentTicketRepository.save(ticket);
        broadcastTicketUpdate(updatedTicket);
    }

    @Override
    @Transactional
    public void returnTicket(ReturnRequest request) {
        RentTicket ticket = rentTicketRepository.findById(request.getTicketId())
                .orElseThrow(() -> new BusinessException("Phiếu mượn không tồn tại!"));

        if (!RentConst.APPROVED.equals(ticket.getStatus()) && !RentConst.RETURNED_WITH_ISSUES.equals(ticket.getStatus())) {
            if (!RentConst.APPROVED.equals(ticket.getStatus()))
                throw new BusinessException("Phiếu chưa được duyệt!");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName());
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticket.getUser().getUserId().equals(currentUser.getUserId());

        if (!isAdmin && !isOwner) throw new AccessDeniedException("Bạn không có quyền trả phiếu này!");

        Map<Integer, ReturnRequest.ReturnItemDetailDto> returnMap = request.getItems().stream()
                .collect(Collectors.toMap(ReturnRequest.ReturnItemDetailDto::getItemId, Function.identity()));

        boolean hasIssue = false;

        for (RentTicketDetail dbDetail : ticket.getDetails()) {
            Item item = dbDetail.getItem();
            ReturnRequest.ReturnItemDetailDto returnDto = returnMap.get(item.getItemId());
            if (returnDto == null) continue;

            BigDecimal returnedQty = returnDto.getQuantityReturned();
            String condition = returnDto.getCondition().trim().toUpperCase();

            if ("GOOD".equals(condition)) {
                item.setCurrentQuantity(item.getCurrentQuantity().add(returnedQty));
            } else if (!"CONSUMED".equals(condition)) {
                if (returnedQty.compareTo(BigDecimal.ZERO) > 0) {
                    item.setCurrentQuantity(item.getCurrentQuantity().add(returnedQty));
                }
            }

            dbDetail.setReturnStatus(condition);
            if (!"GOOD".equals(condition) && !"CONSUMED".equals(condition)) hasIssue = true;
            if (returnedQty.compareTo(dbDetail.getQuantityBorrowed()) < 0) hasIssue = true;

            itemRepository.save(item);
        }

        ticket.setStatus(hasIssue ? RentConst.RETURNED_WITH_ISSUES : RentConst.RETURNED);
        RentTicket savedTicket = rentTicketRepository.save(ticket);
        broadcastTicketUpdate(savedTicket);
    }

    @Override
    @Transactional(readOnly = true)
    public RentDetailResponse getTicketDetail(Integer ticketId) {
        RentTicket ticket = rentTicketRepository.findById(ticketId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy phiếu mượn!"));

        String displayName = ticket.getUser().getUsername();
        if (ticket.getUser().getProfile() != null && ticket.getUser().getProfile().getFullName() != null) {
            displayName = ticket.getUser().getProfile().getFullName();
        }

        RentDetailResponse response = new RentDetailResponse();
        response.setTicketId(ticket.getTicketId());
        response.setRoomName(ticket.getRoom() != null ? ticket.getRoom().getRoomName() : "N/A");
        response.setBorrowDate(ticket.getBorrowDate());
        response.setExpectedReturnDate(ticket.getExpectedReturnDate());
        response.setStatus(ticket.getStatus());
        response.setCreatedDate(ticket.getCreatedDate());
        response.setBorrowerUsername(ticket.getUser().getUsername());
        response.setBorrowerName(displayName);
        response.setDetails(ticket.getDetails());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getRecentActivity() {
        return rentTicketRepository.findTop5ByOrderByCreatedDateDesc().stream()
                .map(rentMapper::toRentListResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getMyRentHistory() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(name);
        return rentTicketRepository.findByUser_UserIdOrderByCreatedDateDesc(user.getUserId()).stream()
                .map(rentMapper::toRentListResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getAllTickets(String status) {
        List<RentTicket> tickets = (status == null || "ALL".equalsIgnoreCase(status))
                ? rentTicketRepository.findAllByOrderByCreatedDateDesc()
                : rentTicketRepository.findByStatusOrderByCreatedDateDesc(status.toUpperCase());
        return tickets.stream().map(rentMapper::toRentListResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RentListResponse> getRentHistoryByUserId(Integer userId) {
        return rentTicketRepository.findByUser_UserIdOrderByCreatedDateDesc(userId).stream()
                .map(rentMapper::toRentListResponse).collect(Collectors.toList());
    }

    private void broadcastTicketUpdate(RentTicket ticket) {
        try {
            messagingTemplate.convertAndSend("/topic/rent-updates", rentMapper.toRentListResponse(ticket));
        } catch (Exception e) {
        }
    }
}