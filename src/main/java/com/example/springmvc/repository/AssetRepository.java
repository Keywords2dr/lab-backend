package com.example.springmvc.repository;

import com.example.springmvc.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Integer> {
    @Query("SELECT a FROM Asset a")
    List<Asset> findAllAssets();

    @Query("SELECT a FROM Asset a WHERE a.name LIKE CONCAT('%', :keyword, '%')")
    List<Asset> searchAssets(@Param("keyword") String keyword);

    Optional<Asset> findByItemCode(String itemCode);
}