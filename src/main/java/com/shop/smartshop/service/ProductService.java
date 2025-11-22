package com.shop.smartshop.service;

import com.shop.smartshop.dto.ProductRequestDTO;
import com.shop.smartshop.dto.ProductResponseDTO;
import com.shop.smartshop.dto.ProductUpdateRequestDTO;
import com.shop.smartshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponseDTO createProduct(ProductRequestDTO productRequest, String username);


    ProductResponseDTO updateProduct(ProductUpdateRequestDTO productUpdateRequestDTO, String username, Long id);

    boolean deleteProduct(Long id);

    Page<ProductResponseDTO> findByDeletedFalse(int page, int size);

    ProductResponseDTO getProductById(Long id);
}
