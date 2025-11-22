package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.dto.CartItemRequestDTO;
import com.shop.smartshop.dto.CartItemResponseDTO;
import com.shop.smartshop.dto.CartItemUpdateRequestDTO;
import com.shop.smartshop.dto.CartResponseDTO;
import com.shop.smartshop.entity.Cart;
import com.shop.smartshop.entity.CartItem;
import com.shop.smartshop.entity.Product;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.exception.BadRequestException;
import com.shop.smartshop.exception.ResourceNotFoundException;
import com.shop.smartshop.repository.CartItemRepository;
import com.shop.smartshop.repository.CartRepository;
import com.shop.smartshop.repository.ProductRepository;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    // request dto me productId, quantity rheti hai ;
    @Override
    public CartResponseDTO addItem(Long id, CartItemRequestDTO requestDTO) {
        User user=userRepository.findById(id).orElse(null);
        // Get Cart Information
        Cart cart=cartRepository.findByUserId(id).orElseGet(()->
        {
            Cart newCart=new Cart();
            newCart.setUser(user);
            newCart.setCreatedAt(LocalDateTime.now());
            newCart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(newCart);
        });
        // Get Product Information
        Product product=productRepository.findById(requestDTO.getProductId()).orElseThrow(()->
        {
            return new ResourceNotFoundException("Products Not Found");
        });
        // Check if already exists in cart
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId((long) cart.getId(), requestDTO.getProductId())
                .orElse(CartItem.builder().
                        cart(cart)
                        .product(product)
                        .priceAtTime(product.getPrice())
                        .quantity(0)
                        .build()
                );

        // --- 5. Update Quantity ---
        int newQty = cartItem.getQuantity() + requestDTO.getQuantity();
        cartItem.setQuantity(newQty);

        // --- 6. Calculate SubTotal ---
        BigDecimal subTotal = BigDecimal
                .valueOf(product.getPrice())
                .multiply(BigDecimal.valueOf(newQty));

        cartItem.setSubTotal(subTotal);
        cartItemRepository.save(cartItem);

        // --- 7. Update Cart Total ---
        double totalAmount = cartItemRepository.sumSubTotalByCartId(cart.getId());
        cart.setTotalAmount(BigDecimal.valueOf(totalAmount));
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return convertToCartResponseDTO(cart);
    }

    @Override
    public CartResponseDTO getAllItems(Long id) {
        Cart cart=cartRepository.findByUserId(id).orElse(null);
        return convertToCartResponseDTO(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO deleteCartItems(Long cartItemId, Long currentUserId) {

        // 1. User cart fetch
        Cart cart = cartRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));

        // 2. Get item
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // 3. Ownership check
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("This item does not belong to your cart");
        }

        // 4. Remove from cart list
        cart.getItems().remove(cartItem);

        // 5. Delete item
        cartItemRepository.delete(cartItem);

        // 6. Recalculate cart total
        BigDecimal newTotal = cart.getItems().stream()
                .map(CartItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(newTotal);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        // 7. Return updated cart DTO
        return convertToCartResponseDTO(cart);
    }
//
//    @Override
//    public CartItemResponseDTO updateItem(Long id, Long currentUserId, CartItemUpdateRequestDTO cartItemUpdateRequestDTO) {
//        // Step 1 : check cart
//        // 1. User cart fetch
//        Cart cart = cartRepository.findByUserId(currentUserId)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
//        // 2. Get item
//        CartItem cartItem = cartItemRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
//
//        // 3. Ownership check
//        if (!cartItem.getCart().getId().equals(cart.getId())) {
//            throw new BadRequestException("This item does not belong to your cart");
//        }
//
//        int oldQty= cartItem.getQuantity();
//        int newQty=oldQty;
//
//        switch (cartItemUpdateRequestDTO.getAction())
//        {
//            case INCREASE -> newQty = oldQty + 1;
//            case DECREASE -> {
//                if (oldQty == 1)
//                    throw new BadRequestException("Minimum quantity is 1");
//                newQty = oldQty - 1;
//            }
//            case SET -> {
//                if (cartItemUpdateRequestDTO.getQuantity() == null)
//                    throw new BadRequestException("Quantity required for SET action");
//                newQty = cartItemUpdateRequestDTO.getQuantity();
//            }
//        }
//
//        // Step 5: Calculate stock difference
//        int stockChange = newQty - oldQty; // + means user wants more, - means user removed some
//
//        Product product = cartItem.getProduct();
//
//        // Step 6: If user is adding more, check stock
//        if (stockChange > 0 && product.getQuantity() < stockChange) {
//            throw new BadRequestException("Not enough stock available");
//        }
//
//
//        // Step 7: Update product stock
//        // Reduce stock if user added quantity
//        // Increase stock if user removed quantity
//        product.setQuantity(product.getQuantity() - stockChange);
//
//
//        productRepository.save(product);
//
//        // Step 8: Update cart item
//        cartItem.setQuantity(newQty);
//        cartItem.setSubTotal(BigDecimal.valueOf(cartItem.getPriceAtTime() * newQty));
//
//        cartItemRepository.save(cartItem);
//
//        // Step 9: Return response
//        return convertToCartResponseDTO(cartItem);
//    }

    @Override
    public CartItemResponseDTO updateItem(Long id, Long userId, CartItemUpdateRequestDTO request) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found"));

        if (!item.getCart().getId().equals(cart.getId()))
            throw new BadRequestException("This item doesn’t belong to you");

        int oldQty = item.getQuantity();
        int newQty = oldQty;

        switch (request.getAction()) {
            case INCREASE -> newQty = oldQty + 1;
            case DECREASE -> {
                if (oldQty == 1)
                    throw new BadRequestException("Min quantity is 1");
                newQty = oldQty - 1;
            }
            case SET -> {
                if (request.getQuantity() == null)
                    throw new BadRequestException("Quantity required for SET action");
                newQty = request.getQuantity();
            }
        }

        // ONLY CHECK STOCK – DO NOT REDUCE IT
        Product product = item.getProduct();
        if (newQty > product.getQuantity()) {
            throw new BadRequestException("Only " + product.getQuantity() + " items available in stock");
        }

        item.setQuantity(newQty);
        item.setSubTotal(BigDecimal.valueOf(item.getPriceAtTime() * newQty));
        cartItemRepository.save(item);

        return convertToCartResponseDTO(item);
    }



    private CartItemResponseDTO convertToCartResponseDTO(CartItem cartItem) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setItemId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setPriceAtTime(cartItem.getPriceAtTime());
        dto.setQuantity(cartItem.getQuantity());
        dto.setSubTotal(cartItem.getSubTotal());
        return dto;
    }


    private CartResponseDTO convertToCartResponseDTO(Cart cart) {
        List<CartItemResponseDTO> items = cart.getItems()
                .stream()
                .map(item -> CartItemResponseDTO.builder()
                        .itemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .priceAtTime(item.getPriceAtTime())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal())
                        .build())
                .toList();


        return CartResponseDTO.builder()
                .cartId(cart.getId())
                .items(items)
                .totalAmount(cart.getTotalAmount())
                .currency("INR")
                .status("ACTIVE")
                .build();
    }


}
