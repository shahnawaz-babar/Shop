package com.shop.smartshop.service;

import com.shop.smartshop.dto.CartItemRequestDTO;
import com.shop.smartshop.dto.CartItemResponseDTO;
import com.shop.smartshop.dto.CartItemUpdateRequestDTO;
import com.shop.smartshop.dto.CartResponseDTO;
import jakarta.validation.Valid;

public interface CartService {

    CartResponseDTO addItem(Long id, @Valid CartItemRequestDTO requestDTO);

    CartResponseDTO getAllItems(Long id);

    CartResponseDTO deleteCartItems(Long id, Long currentUserId);

    CartItemResponseDTO updateItem(Long id, Long currentUserId, CartItemUpdateRequestDTO cartItemUpdateRequestDTO);
}
