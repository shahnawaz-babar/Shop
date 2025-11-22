package com.shop.smartshop.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class LoginRequestByPasswordDTO {

    private String name;
    private String password;

}
