package com.shop.smartshop.repository;


import com.shop.smartshop.entity.RecentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecentViewRepository extends JpaRepository<RecentView,Long> {

    List<RecentView> findTop5ByUserIdOrderByViewedAtDesc(Long userId);

}
