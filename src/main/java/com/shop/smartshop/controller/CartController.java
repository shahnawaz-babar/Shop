package com.shop.smartshop.controller;

import com.shop.smartshop.dto.*;
import com.shop.smartshop.entity.Cart;
import com.shop.smartshop.entity.CartItem;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.repository.CartRepository;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.security.UserPrincipal;
import com.shop.smartshop.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cart")
@RestController
@Slf4j
@RequiredArgsConstructor
public class    CartController   {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @PostMapping("/add_items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> addItems(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestBody @Valid CartItemRequestDTO requestDTO
            )
    {
        String username = currentUser.getUsername();
        System.out.println("Current User login : "+username);
        User user = userRepository.findByEmail(username).orElse(null);
        System.out.println("User Details: "+user);
        CartResponseDTO responseDTO=cartService.addItem(user.getId(),requestDTO);
        return ResponseEntity.ok(ApiResponse.success("",responseDTO));
    }


    @GetMapping("/get_all_items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAllItems(@AuthenticationPrincipal UserDetails currentUser)
    {
        System.out.println("User information: "+currentUser);
        log.debug("Fetching cart for userId={} page={} size={}", currentUser.getUsername());
        User user=userRepository.findByEmail( currentUser.getUsername()).orElse(null);
        CartResponseDTO cart=cartService.getAllItems(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Successfully retrive all Items",cart));
    }

    @DeleteMapping("/delete_item_in_cart/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser)
    {
        log.info("Deleting item {} for user {}", id, currentUser.getId());
        CartResponseDTO updatedCart = cartService.deleteCartItems(id, currentUser.getId());
        return ResponseEntity.ok(
                ApiResponse.success("Item deleted successfully", updatedCart)
        );
    }


    @PutMapping("/remove_items_in_cart/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeItemd(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser, @RequestBody CartItemUpdateRequestDTO cartItemUpdateRequestDTO)
    {
        log.info("Updating Items  Quantity {} for user {}",id,currentUser.getId());
        CartItemResponseDTO cartItemResponseDTO=cartService.updateItem(id,currentUser.getId(),cartItemUpdateRequestDTO);
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", cartItemResponseDTO));
    }

}
