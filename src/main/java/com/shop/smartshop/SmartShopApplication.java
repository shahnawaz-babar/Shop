package com.shop.smartshop;

import com.shop.smartshop.config.GoogleProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(GoogleProperties.class)
public class SmartShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartShopApplication.class, args);
	}

}
