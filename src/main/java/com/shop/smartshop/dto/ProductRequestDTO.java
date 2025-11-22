package com.shop.smartshop.dto;

import com.shop.smartshop.entity.Category;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.enums.Stock;
import com.shop.smartshop.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    private String name;
    private String description;
    private double price;
    private int quantity;
    private Stock stock;      // IN_STOCK / OUT_OF_STOCK / LOW_STOCK
    private String imageUrl;
    private Unit unit;        // KG / LITRE / PCS
    private String brand;
    private Long categoryId;   // Only ID, not Category object
    private User user ;
}
