package com.shop.smartshop.dto;

import com.shop.smartshop.enums.Stock;
import com.shop.smartshop.enums.Unit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class  ProductUpdateRequestDTO {

    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Stock stock;
    private String imageUrl;
    private Unit unit;
    private String brand;
    private Long categoryId;   // because Category is entity, but DTO should use IDs
}
