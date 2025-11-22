package com.shop.smartshop.schedular;


import com.shop.smartshop.entity.Product;
import com.shop.smartshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertySchedular {

    private final ProductRepository productRepository;

    // Runs every 30 minutes
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void deleteSoftDeletedProducts() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(20);
        log.info("🔄 Running soft-deleted product cleaner... Time cutoff: {}", cutoffTime);
        List<Product> productsToDelete = productRepository.findProductsToHardDelete(cutoffTime);
        if (productsToDelete.isEmpty()) {
            log.info("ℹ No products found for hard delete.");
        } else {
            int count = productsToDelete.size();
            productRepository.deleteAll(productsToDelete);

            log.info("🗑 Hard deleted {} products older than 20 minutes.", count);

            // Optional: list their IDs
            productsToDelete.forEach(p ->
                    log.info("➡ Deleted Product ID: {}", p.getId())
            );
        }
        log.info("✅ Soft-deleted cleanup job completed.");
    }


}
