package com.shop.smartshop.dto;

import com.shop.smartshop.entity.User;
import com.shop.smartshop.enums.Stock;
import com.shop.smartshop.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private Stock stock;
    private String imageUrl;
    private Unit unit;
    private String brand;

    // Category response (minimal safe fields)
    private Long categoryId;
    private String categoryName;

    // User info (safe)
    private Long createdByUserId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
    private long views;

    private UserSummaryDTO userSummary;
}
