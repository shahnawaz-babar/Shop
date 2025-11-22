CREATE TABLE recent_views (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recent_view_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_recent_view_product FOREIGN KEY (product_id) REFERENCES products(id)
);
