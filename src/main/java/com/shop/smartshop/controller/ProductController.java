package com.shop.smartshop.controller;

import com.shop.smartshop.dto.*;
import com.shop.smartshop.entity.Product;
import com.shop.smartshop.entity.RecentView;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.exception.BadRequestException;
import com.shop.smartshop.exception.ResourceNotFoundException;
import com.shop.smartshop.repository.ProductRepository;
import com.shop.smartshop.repository.RecentViewRepository;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.service.ProductService;
import com.shop.smartshop.service.RecentViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RecentViewService recentViewService;
    private final RecentViewRepository recentViewRepository;



    // add Product
    @PostMapping("/add_product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> addProduct(@RequestBody ProductRequestDTO productRequest, @AuthenticationPrincipal UserDetails currentUser)
    {
        System.out.println(currentUser);
        log.info("Entering addProduct method.");
        log.debug("Product Request DTO received: {}", productRequest);
        log.info("Attempting to create product for user ID: {}", currentUser.getUsername());
        String username= currentUser.getUsername();
        ProductResponseDTO savedProductRequest = productService.createProduct(productRequest,username);
        log.info("Product successfully created. New Product ID: {}", savedProductRequest.getId());
        log.debug("Product Response DTO details: {}", savedProductRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully",savedProductRequest));
    }

    // update Product
    @PutMapping("/update_product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequestDTO productUpdateRequestDTO, @AuthenticationPrincipal UserDetails currentUser )
    {
        log.info("trying to fetch Product details ");
        Product product=productRepository.findById(id).orElseThrow( () ->  {
            log.warn("Product does not exist {} ",id);
            return new BadRequestException("Product does not exit with this id");
        });

        log.info("Enter in Product update method");
        String username= currentUser.getUsername();
        ProductResponseDTO savedProductRequest=productService.updateProduct(productUpdateRequestDTO,username,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product Updated Successfully",savedProductRequest));
    }

    // delete Product
    @PutMapping("/delete_product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id,@AuthenticationPrincipal UserDetails user)
    {
        log.info("enter in delete product controller ");
        boolean flag=false;
        flag=productService.deleteProduct(id);
        if(flag)
        {
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Product Deleted Successfully"));
        }
        else
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.success("Product is not Deleted Successfully"));
        }
    }

    // get All Product
    @GetMapping("/all_product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> allProduct(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)
    {
        log.info("Getting all Products ");
        Page<ProductResponseDTO> products=productService.findByDeletedFalse(page,size);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrive all property",products));
    }

    // get Product By Id
    @GetMapping("/productById/{id}")
    public ResponseEntity<ApiResponse<?>> getProductById(@PathVariable Long id,@AuthenticationPrincipal UserDetails currentUser)
    {
        String username = currentUser.getUsername();
        System.out.println("Current User login : "+username);
        User user = userRepository.findByEmail(username).orElse(null);
        System.out.println("User Details: "+user);
        // record recent view
        recentViewService.addRecentView(user.getId(), id);
        log.info("Get Product By Id");
        ProductResponseDTO dto = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", dto));
    }

    @GetMapping("/product-recent-views")
    public ResponseEntity<ApiResponse<?>> getRecentViews(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Enter in Recent View Controller");
        Long userId = userRepository.findByEmail(userDetails.getUsername()).get().getId();

        List<RecentViewDTO> response = recentViewService.getRecentViews(userId);
        return ResponseEntity.ok(ApiResponse.success("Recent views", response));
    }


}
