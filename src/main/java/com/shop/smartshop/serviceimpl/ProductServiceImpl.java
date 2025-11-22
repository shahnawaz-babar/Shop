package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.dto.ProductRequestDTO;
import com.shop.smartshop.dto.ProductResponseDTO;
import com.shop.smartshop.dto.ProductUpdateRequestDTO;
import com.shop.smartshop.dto.UserSummaryDTO;
import com.shop.smartshop.entity.Category;
import com.shop.smartshop.entity.Product;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.exception.BadRequestException;
import com.shop.smartshop.exception.ResourceNotFoundException;
import com.shop.smartshop.repository.CategoryRepository;
import com.shop.smartshop.repository.ProductRepository;
import com.shop.smartshop.repository.UserRepository;
import com.shop.smartshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequest, String username) {
        // 1. Log method entry and requested category ID
        log.info("Entering createProduct service method.");
        log.debug("ProductRequest DTO: {}", productRequest);
        log.info("Fetching Category with ID: {}", productRequest.getCategoryId());

        User user=userRepository.findByEmail(username).orElse(null);

        // Check for null category ID
        if (productRequest.getCategoryId() == null) {
            log.error("Category ID is missing in the request.");
            throw new BadRequestException("Category ID cannot be null");
        }

        // Find Category or throw exception
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("ResourceNotFoundException: Category ID {} not found.", productRequest.getCategoryId());
                    return new ResourceNotFoundException("Category not found");
                });

        // Log successful category fetch
        log.info("Category found: {}", category.getName());

        // Build the Product entity
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .unit(productRequest.getUnit())
                .stock(productRequest.getStock())
                .brand(productRequest.getBrand())
                .imageUrl(productRequest.getImageUrl())
                .category(category)
                .user(user)
                .build();

        // Log the entity before saving
        log.debug("Built Product entity before save: {}", product);

        // Save the product to the database
        productRepository.save(product);

        // Log successful save operation
        log.info("Product entity saved successfully to database. New ID: {}", product.getId());

        // Convert and return the response DTO
        ProductResponseDTO responseDTO = toResponse(product);

        // Log method exit
        log.info("Exiting createProduct service method.");

        return responseDTO;
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(ProductUpdateRequestDTO dto, String username, Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product Not Found"));
        // Update only if not null
        if (dto.getName() != null) product.setName(dto.getName());
        if (dto.getDescription() != null) product.setDescription(dto.getDescription());
        if (dto.getPrice() != null) product.setPrice(dto.getPrice());
        if (dto.getQuantity() != null) product.setQuantity(dto.getQuantity());
        if (dto.getStock() != null) product.setStock(dto.getStock());
        if (dto.getImageUrl() != null) product.setImageUrl(dto.getImageUrl());
        if (dto.getUnit() != null) product.setUnit(dto.getUnit());
        if (dto.getBrand() != null) product.setBrand(dto.getBrand());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category Not Found"));
            product.setCategory(category);
        }

        // updatedAt automatically updated from @PreUpdate
        Product updated = productRepository.save(product);

        return toResponse(updated);
    }

    @Override
    public boolean deleteProduct(Long id) {
        Product product=productRepository.findById(id).orElseThrow( () ->  {
            log.warn("Product does not exist {} ",id);
            return new BadRequestException("Product does not exit with this id");
        });
        product.setDeleted(true);
        product.setDeletedTime(LocalDateTime.now());
        productRepository.save(product);
        return true;
    }

    @Override
    public Page<ProductResponseDTO> findByDeletedFalse(int page, int size) {
        Pageable pageable=PageRequest.of(page,size, Sort.by("id").descending());
        Page<Product> productPage = productRepository.findByDeletedFalse(pageable);
        return productPage.map(this::convertToDTO);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + id));

        // Increase view count
        product.setViewCount(product.getViewCount() + 1);
        productRepository.save(product);

        return convertToDTO(product);    }


    @Transactional
    public ProductResponseDTO toResponse(Product product) {
        // 1. Log method entry and the ID of the entity being converted
        log.debug("Entering toResponse conversion method for Product ID: {}", product.getId());
        //User user=userRepository.findByEmail()
        ProductResponseDTO responseDTO = ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .stock(product.getStock())
                .imageUrl(product.getImageUrl())
                .unit(product.getUnit())
                .brand(product.getBrand())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .createdByUserId(product.getUser().getId())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .views(product.getViewCount())
                .userSummary(mapToUserSummaryDto(product.getUser()))
                .build();

        // 2. Log the resulting DTO and method exit
        log.debug("Successfully converted Product ID {} to Response DTO: {}", product.getId(), responseDTO);
        log.debug("Exiting toResponse conversion method.");

        return responseDTO;
    }

    private ProductResponseDTO convertToDTO(Product p) {
        return ProductResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .stock(p.getStock())
                .imageUrl(p.getImageUrl())
                .unit(p.getUnit())
                .brand(p.getBrand())
                .categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .createdByUserId(p.getUser() != null ? p.getUser().getId() : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .views(p.getViewCount())
                .build();
    }


    private UserSummaryDTO mapToUserSummaryDto(User user) {
        return UserSummaryDTO.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
                .phone(user.getMobileNumber()).profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())).build();
    }

}
