package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.dto.CreateOrderRequest;
import com.shop.smartshop.dto.OrderItemResponseDTO;
import com.shop.smartshop.dto.OrderResponseDTO;
import com.shop.smartshop.entity.*;
import com.shop.smartshop.enums.OrderStatus;
import com.shop.smartshop.enums.PaymentStatus;
import com.shop.smartshop.exception.BadRequestException;
import com.shop.smartshop.exception.ResourceNotFoundException;
import com.shop.smartshop.repository.AddressRepository;
import com.shop.smartshop.repository.CartRepository;
import com.shop.smartshop.repository.OrderRepository;
import com.shop.smartshop.repository.ProductRepository;
import com.shop.smartshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderResponseDTO placeOrder(Long currentUserId, CreateOrderRequest request) {

        // 1. Cart fetch
        Cart cart=cartRepository.findByUserId(currentUserId).orElseThrow(()-> new ResourceNotFoundException("Cart not found"));

        // check cart is not empty
        if(cart.getItems() == null || cart.getItems().isEmpty())
        {
            throw new BadRequestException("Cart is empty");
        }

        // 2. Address fetch
        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Shipping address not found"));

        //create new Order
        Order order=new Order();
        order.setUser(cart.getUser());
        order.setAddress(shippingAddress);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setTotalAmount(BigDecimal.ZERO);
        order=orderRepository.save(order); // save first so we have id


        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems=new ArrayList<>();

        // 4. For each cart item, validate stock and build order item
        for(CartItem cartItem:cart.getItems())
        {
            // 4a. Lock product row
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + cartItem.getProduct().getId()));

            int requestedQty=cartItem.getQuantity();

            if (product.getQuantity() < requestedQty) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            // 4b. Reduce stock NOW (atomic within transaction)
            product.setQuantity(product.getQuantity()-requestedQty);
            productRepository.save(product);

            // 4c. Create OrderItem
            BigDecimal subTotal = BigDecimal.valueOf(cartItem.getPriceAtTime() * requestedQty);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .priceAtOrder(cartItem.getPriceAtTime())
                    .quantity(requestedQty)
                    .subTotal(subTotal)
                    .build();

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(subTotal);

        }


        // 5. Attach items + total to order
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        // 6. Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);


        return convertToOrderResponseDTO(order);
    }

    private OrderResponseDTO convertToOrderResponseDTO(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setOrderId(order.getId());
        dto.setStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());

        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream().map(oi -> {
            OrderItemResponseDTO i = new OrderItemResponseDTO();
            i.setProductId(oi.getProduct().getId());
            i.setProductName(oi.getProduct().getName());
            i.setPriceAtTime(oi.getProduct().getPrice());
            i.setQuantity(oi.getQuantity());
            i.setSubTotal(oi.getSubTotal());
            return i;
        }).toList();
        dto.setItems(itemDTOs);
        return dto;
    }
}
