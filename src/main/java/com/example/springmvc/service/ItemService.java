package com.example.springmvc.service;

import com.example.springmvc.dto.item.ItemResponse;
import java.util.List;

public interface ItemService {
    List<ItemResponse> searchItems(String category, String keyword, boolean fetchStock);
}