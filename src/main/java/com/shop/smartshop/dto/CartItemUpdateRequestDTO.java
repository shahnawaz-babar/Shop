package com.shop.smartshop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateRequestDTO {

    @NotNull(message = "Action is required")
    private Action action;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;   // Used only if SET action

    public enum Action {
        INCREASE,
        DECREASE,
        SET       // Replace quantity with given value
    }
}
