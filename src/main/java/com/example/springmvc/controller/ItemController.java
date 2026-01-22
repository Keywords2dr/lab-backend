package com.example.springmvc.controller;

import com.example.springmvc.dto.item.ItemResponse;
import com.example.springmvc.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getItems(
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "fetchStock", required = false, defaultValue = "false") boolean fetchStock
    ) {
        List<ItemResponse> result = itemService.searchItems(category, keyword, fetchStock);
        return ResponseEntity.ok(result);
    }
}