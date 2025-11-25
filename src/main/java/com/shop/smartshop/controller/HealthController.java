package com.shop.smartshop.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/health")
public class HealthController {

    @GetMapping
    public String healthCheck()
    {
        return "Backend is Running";
    }

}
