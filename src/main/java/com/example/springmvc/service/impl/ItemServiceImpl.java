package com.example.springmvc.service.impl;

import com.example.springmvc.dto.item.ItemResponse;
import com.example.springmvc.entity.Item;
import com.example.springmvc.mapper.ItemMapper;
import com.example.springmvc.repository.ItemRepository;
import com.example.springmvc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponse> searchItems(String category, String keyword, boolean fetchStock) {
        List<Item> rawItems = queryItems(category, keyword);
        List<ItemResponse> dtos = convertToDtoList(rawItems);

        if (!fetchStock) {
            dtos.forEach(dto -> dto.setAvailableQuantity(null));
        }
        return dtos;
    }

    private List<Item> queryItems(String category, String keyword) {
        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasCategory = isValidCategory(category);

        if (hasCategory && hasKeyword) {
            return itemRepository.searchByCategoryAndKeyword(category.trim(), keyword.trim());
        }
        if (hasKeyword) {
            return itemRepository.searchGlobal(keyword.trim());
        }
        if (hasCategory) {
            return itemRepository.findByCategoryType(category.trim());
        }
        return itemRepository.findAll();
    }

    private List<ItemResponse> convertToDtoList(List<Item> items) {
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean isValidCategory(String category) {
        return StringUtils.hasText(category) && !category.equalsIgnoreCase("ALL");
    }
}