package com.shop.smartshop.repository;

import com.shop.smartshop.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession,Long> {
}
