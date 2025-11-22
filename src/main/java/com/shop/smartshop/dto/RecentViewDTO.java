package com.shop.smartshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentViewDTO {
    private Long productId;
    private String productName;
    private String productImage;
    private LocalDateTime viewedAt;
}
