package com.example.springmvc.mapper;

import com.example.springmvc.dto.rent.RentListResponse;
import com.example.springmvc.entity.RentTicket;
import com.example.springmvc.entity.User;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RentMapper {

    @Mapping(source = "room.roomName", target = "roomName")
    @Mapping(source = "user", target = "borrowerName", qualifiedByName = "getBorrowerName")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(target = "itemSummary", ignore = true)
    RentListResponse toRentListResponse(RentTicket entity);

    @Named("getBorrowerName")
    default String getBorrowerName(User user) {
        if (user == null) return "N/A";
        if (user.getProfile() != null && user.getProfile().getFullName() != null) {
            return user.getProfile().getFullName();
        }
        return user.getUsername();
    }

    @AfterMapping
    default void mapItemSummary(@MappingTarget RentListResponse dto, RentTicket entity) {
        if (entity.getDetails() == null || entity.getDetails().isEmpty()) {
            dto.setItemSummary("Không có vật tư");
            return;
        }

        String summary = entity.getDetails().stream()
                .limit(3)
                .map(d -> {
                    String unit = d.getItem().getUnit() == null ? "" : d.getItem().getUnit();
                    String quantity = d.getQuantityBorrowed().stripTrailingZeros().toPlainString();
                    return d.getItem().getName() + " (" + quantity + " " + unit + ")";
                })
                .collect(Collectors.joining(", "));

        if (entity.getDetails().size() > 3) {
            summary += ", ...";
        }

        dto.setItemSummary(summary);
    }
}