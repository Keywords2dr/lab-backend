package com.example.springmvc.repository;

import com.example.springmvc.entity.Chemical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChemicalRepository extends JpaRepository<Chemical, Integer> {

    @Query("SELECT c FROM Chemical c")
    List<Chemical> findAllChemicals();

    @Query("SELECT c FROM Chemical c WHERE c.currentQuantity < :threshold")
    List<Chemical> findLowStockChemicals(@Param("threshold") BigDecimal threshold);

    Optional<Chemical> findByItemCode(String itemCode);
}