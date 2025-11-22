package com.shop.smartshop.serviceimpl;

import com.shop.smartshop.dto.RecentViewDTO;
import com.shop.smartshop.entity.Product;
import com.shop.smartshop.entity.RecentView;
import com.shop.smartshop.entity.User;
import com.shop.smartshop.repository.RecentViewRepository;
import com.shop.smartshop.service.RecentViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class RecentViewProductServiceImpl implements RecentViewService {

    private final RecentViewRepository recentViewRepository;

    public void addRecentView(Long userId, Long productId) {

        RecentView recentView = RecentView.builder()
                .user(User.builder().id(userId).build())
                .product(Product.builder().id(productId).build())
                .viewedAt(LocalDateTime.now())
                .build();

        recentViewRepository.save(recentView);
    }

    @Override
    public List<RecentViewDTO> getRecentViews(Long userId) {
        List<RecentView> views = recentViewRepository.findTop5ByUserIdOrderByViewedAtDesc(userId);
        return views.stream().map(v -> RecentViewDTO.builder()
                .productId(v.getProduct().getId())
                .productName(v.getProduct().getName())
                .productImage(v.getProduct().getImageUrl())
                .viewedAt(v.getViewedAt())
                .build()
        ).toList();
    }

}
