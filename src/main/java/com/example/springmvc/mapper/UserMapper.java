package com.example.springmvc.mapper;

import com.example.springmvc.dto.user.UserProfileResponse;
import com.example.springmvc.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "profile.fullName", target = "fullName")
    @Mapping(source = "profile.email", target = "email")
    @Mapping(source = "profile.phoneNumber", target = "phoneNumber")
    @Mapping(source = "profile.department", target = "department")
    @Mapping(source = "profile.faculty", target = "faculty")
    @Mapping(source = "profile.avatar", target = "avatar")
    UserProfileResponse toProfileDto(User user);
}