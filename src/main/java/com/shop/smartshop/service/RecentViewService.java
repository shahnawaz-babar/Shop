package com.shop.smartshop.service;

import com.shop.smartshop.dto.RecentViewDTO;
import org.springframework.stereotype.Service;

import java.util.List;

public interface RecentViewService {
    void addRecentView(Long id, Long id1);

    List<RecentViewDTO> getRecentViews(Long userId);
}
