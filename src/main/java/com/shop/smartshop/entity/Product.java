package com.shop.smartshop.entity;

import com.shop.smartshop.enums.Stock;
import com.shop.smartshop.enums.Unit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products") //  safe table name
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //  Primary key
    private String name;              //  Product name
    private String description;       //  Short or long description
    private double price;         //  Use BigDecimal for money (not String/double)
    private int quantity;             //  Total stock quantity
    @Enumerated(EnumType.STRING)
    private Stock stock;              //  Enum: IN_STOCK / OUT_OF_STOCK / LOW_STOCK
    @Column(name = "image_url")  // ya "imageUrl" — DB mein jo actual naam hai woh daal
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private Unit unit;                //  Enum: KG / LITRE / PCS etc.
    private String brand;             //  Optional field for brand name
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    private boolean deleted = false;
    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;
    private LocalDateTime createdAt;  //  Record creation time
    private LocalDateTime updatedAt;  //  Record last update time
    private long viewCount = 0;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
