package com.example.springmvc.repository;

import com.example.springmvc.entity.UserSecuritySettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSecuritySettingsRepository extends JpaRepository<UserSecuritySettings, Integer> {
    Optional<UserSecuritySettings> findByUser_UserId(Integer userId);
}