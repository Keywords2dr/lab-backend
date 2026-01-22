package com.example.springmvc.repository;

import com.example.springmvc.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByCategoryType(String categoryType);

    @Query("SELECT i FROM Item i WHERE i.name LIKE CONCAT('%', :keyword, '%')")
    List<Item> searchGlobal(@Param("keyword") String keyword);

    @Query("SELECT i FROM Item i WHERE i.categoryType = :category AND i.name LIKE CONCAT('%', :keyword, '%')")
    List<Item> searchByCategoryAndKeyword(@Param("category") String category, @Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Item i SET " +
            "i.currentQuantity = i.currentQuantity - :quantity, " +
            "i.lockedQuantity = COALESCE(i.lockedQuantity, 0) + :quantity " +
            "WHERE i.itemId = :itemId AND i.currentQuantity >= :quantity")
    int reserveStock(@Param("itemId") Integer itemId, @Param("quantity") BigDecimal quantity);
}