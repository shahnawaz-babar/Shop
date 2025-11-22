package com.shop.smartshop.repository;

import com.shop.smartshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Query("SELECT COALESCE(SUM(c.subTotal), 0) FROM CartItem c WHERE c.cart.id = :cartId")
    double sumSubTotalByCartId(Long cartId);

}
