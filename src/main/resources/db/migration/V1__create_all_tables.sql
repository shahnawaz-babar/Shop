-- V1__create_all_tables.sql

-- USERS TABLE
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  address VARCHAR(255),
  email VARCHAR(255),
  mobile_number VARCHAR(255),
  mobile_verified BIT(1) NOT NULL,
  name VARCHAR(255),
  password VARCHAR(255),
  profile_image_url VARCHAR(512),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- CATEGORY TABLE
CREATE TABLE category (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ADDRESS TABLE
CREATE TABLE address (
  id BIGINT NOT NULL AUTO_INCREMENT,
  city VARCHAR(255),
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  pincode BIGINT NOT NULL,
  state VARCHAR(255),
  street VARCHAR(255),
  user_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY UK_address_user (user_id),
  CONSTRAINT FK_address_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- CART TABLE
CREATE TABLE cart (
  id INT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6),
  total_amount DECIMAL(38,2),
  updated_at DATETIME(6),
  user_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY UK_cart_user (user_id),
  CONSTRAINT FK_cart_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- PRODUCTS TABLE
CREATE TABLE products (
  id BIGINT NOT NULL AUTO_INCREMENT,
  brand VARCHAR(255),
  created_at DATETIME(6),
  description VARCHAR(255),
  image_url VARCHAR(255),
  name VARCHAR(255),
  price DOUBLE NOT NULL,
  quantity INT NOT NULL,
  stock ENUM('IN_STOCK','LOW_STOCK','OUT_OF_STOCK'),
  unit ENUM('BOTTLE','DOZEN','GRAM','JAR','KILOGRAM','LITER','LOAF','MILLILITER','PACK'),
  updated_at DATETIME(6),
  category_id BIGINT,
  user_id BIGINT,
  PRIMARY KEY (id),
  KEY FK_products_category (category_id),
  KEY FK_products_user (user_id),
  CONSTRAINT FK_products_category FOREIGN KEY (category_id) REFERENCES category(id),
  CONSTRAINT FK_products_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- CART ITEM TABLE
CREATE TABLE cart_item (
  id BIGINT NOT NULL AUTO_INCREMENT,
  price_at_time DOUBLE NOT NULL,
  quantity INT NOT NULL,
  sub_total DOUBLE NOT NULL,
  cart_id INT,
  product_id BIGINT,
  PRIMARY KEY (id),
  KEY FK_cartitem_cart (cart_id),
  KEY FK_cartitem_product (product_id),
  CONSTRAINT FK_cartitem_cart FOREIGN KEY (cart_id) REFERENCES cart(id),
  CONSTRAINT FK_cartitem_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

-- COUPEN TABLE
CREATE TABLE coupen (
  id BIGINT NOT NULL AUTO_INCREMENT,
  code VARCHAR(255),
  discount_percentage DOUBLE NOT NULL,
  expiration_date DATETIME(6),
  is_active BIT(1) NOT NULL,
  user_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY UK_coupen_user (user_id),
  CONSTRAINT FK_coupen_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- OTP TABLE
CREATE TABLE otps (
  id BIGINT NOT NULL AUTO_INCREMENT,
  blocked BIT(1) NOT NULL,
  code VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  expires_at DATETIME(6),
  identifier VARCHAR(255) NOT NULL,
  mobile_number VARCHAR(255) NOT NULL,
  verified BIT(1) NOT NULL,
  verified_at DATETIME(6),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ORDERS TABLE
CREATE TABLE orders (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6),
  order_status TINYINT,
  payment_method VARCHAR(255),
  payment_status TINYINT,
  tracking_latitude VARCHAR(255),
  tracking_longitude VARCHAR(255),
  updated_at DATETIME(6),
  address_id BIGINT,
  user_id BIGINT,
  PRIMARY KEY (id),
  UNIQUE KEY UK_order_address (address_id),
  UNIQUE KEY UK_order_user (user_id),
  CONSTRAINT FK_order_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT FK_order_address FOREIGN KEY (address_id) REFERENCES address(id),
  CONSTRAINT chk_order_status CHECK (order_status BETWEEN 0 AND 8),
  CONSTRAINT chk_payment_status CHECK (payment_status BETWEEN 0 AND 2)
) ENGINE=InnoDB;

-- ORDER ITEM TABLE
CREATE TABLE order_item (
  id INT NOT NULL AUTO_INCREMENT,
  price_at_order VARCHAR(255),
  quantity INT NOT NULL,
  sub_total DOUBLE NOT NULL,
  order_id BIGINT,
  product_id BIGINT,
  PRIMARY KEY (id),
  KEY FK_orderitem_order (order_id),
  KEY FK_orderitem_product (product_id),
  CONSTRAINT FK_orderitem_product FOREIGN KEY (product_id) REFERENCES products(id),
  CONSTRAINT FK_orderitem_order FOREIGN KEY (order_id) REFERENCES orders(id)
) ENGINE=InnoDB;

-- USER ROLES TABLE
CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role ENUM('ADMIN','SUBADMIN','USER'),
  KEY FK_userroles_user (user_id),
  CONSTRAINT FK_userroles_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- USER SESSIONS TABLE
CREATE TABLE user_sessions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  active BIT(1) NOT NULL,
  created_at DATETIME(6),
  device_info VARCHAR(255),
  expires_at DATETIME(6),
  ip_address VARCHAR(255) NOT NULL,
  last_accessed_at DATETIME(6),
  session_id VARCHAR(255) NOT NULL,
  updated_at DATETIME(6),
  user_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  KEY FK_usersessions_user (user_id),
  CONSTRAINT FK_usersessions_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB;
