-- Library Management System Database Schema and Setup Script
-- Copy from Library.sql and include sample data
DROP DATABASE IF EXISTS library_db;

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `email` varchar(255) UNIQUE NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'customer' COMMENT 'customer|admin',
  `phone` varchar(30),
  `address` text,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `categories` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) UNIQUE NOT NULL,
  `description` text,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `products` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `description` longtext,
  `original_price` decimal(12,2) NOT NULL,
  `price` decimal(12,2) NOT NULL,
  `stock` int NOT NULL DEFAULT 0,
  `material` varchar(100),
  `category_id` bigint NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `product_images` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `image_url` varchar(2048) NOT NULL,
  `created_at` datetime NOT NULL
);

CREATE TABLE `product_discounts` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `discount_type` varchar(20) NOT NULL COMMENT 'percentage|fixed',
  `discount_value` decimal(12,2) NOT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'draft' COMMENT 'draft|active|inactive|expired',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `vouchers` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `code` varchar(50) UNIQUE NOT NULL,
  `discount_type` varchar(20) NOT NULL COMMENT 'percentage|fixed',
  `discount_value` decimal(12,2) NOT NULL,
  `min_order_value` decimal(12,2) NOT NULL DEFAULT 0,
  `max_discount` decimal(12,2),
  `quantity` int NOT NULL DEFAULT 0,
  `used_count` int NOT NULL DEFAULT 0,
  `per_user_limit` int NOT NULL DEFAULT 1,
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'draft' COMMENT 'draft|active|inactive|expired',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `carts` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint UNIQUE NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `cart_items` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL
);

CREATE TABLE `orders` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `total_price` decimal(12,2) NOT NULL,
  `discount_amount` decimal(12,2) NOT NULL DEFAULT 0,
  `final_price` decimal(12,2) NOT NULL,
  `voucher_id` bigint,
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT 'pending|shipping|completed|cancelled',
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

CREATE TABLE `order_items` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `price` decimal(12,2) NOT NULL COMMENT 'price at purchase time (after product discount if any)',
  `quantity` int NOT NULL
);

CREATE TABLE `payments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint UNIQUE NOT NULL,
  `method` varchar(20) NOT NULL COMMENT 'COD|banking',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT 'pending|paid|failed|refunded|cancelled',
  `paid_at` datetime
);

CREATE TABLE `voucher_usage` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `voucher_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `order_id` bigint UNIQUE NOT NULL,
  `used_at` datetime NOT NULL
);

CREATE TABLE `reviews` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `rating` tinyint NOT NULL COMMENT '1..5',
  `comment` text,
  `created_at` datetime NOT NULL
);

CREATE TABLE `product_views` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint COMMENT 'nullable for anonymous views',
  `product_id` bigint NOT NULL,
  `viewed_at` datetime NOT NULL
);

CREATE TABLE `borrows` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `borrow_date` datetime NOT NULL,
  `due_date` datetime NOT NULL,
  `return_date` datetime,
  `status` varchar(20) NOT NULL DEFAULT 'borrowing' COMMENT 'borrowing|returned|overdue',
  `fine_amount` decimal(12,2),
  `fine_reason` text,
  `notes` text,
  `created_at` datetime NOT NULL,
  `updated_at` datetime NOT NULL
);

-- Indexes
CREATE UNIQUE INDEX `uk_users_email` ON `users` (`email`);
CREATE INDEX `idx_users_role` ON `users` (`role`);
CREATE UNIQUE INDEX `uk_categories_name` ON `categories` (`name`);
CREATE INDEX `idx_products_category_id` ON `products` (`category_id`);
CREATE INDEX `idx_products_price` ON `products` (`price`);
CREATE INDEX `idx_products_material` ON `products` (`material`);
CREATE INDEX `idx_product_images_product_id` ON `product_images` (`product_id`);
CREATE INDEX `idx_product_discounts_product_id` ON `product_discounts` (`product_id`);
CREATE INDEX `idx_product_discounts_lookup` ON `product_discounts` (`product_id`, `status`, `start_date`, `end_date`);
CREATE UNIQUE INDEX `uk_vouchers_code` ON `vouchers` (`code`);
CREATE INDEX `idx_vouchers_active_window` ON `vouchers` (`status`, `start_date`, `end_date`);
CREATE UNIQUE INDEX `uk_carts_user_id` ON `carts` (`user_id`);
CREATE UNIQUE INDEX `uk_cart_items_cart_product` ON `cart_items` (`cart_id`, `product_id`);
CREATE INDEX `idx_cart_items_cart_id` ON `cart_items` (`cart_id`);
CREATE INDEX `idx_cart_items_product_id` ON `cart_items` (`product_id`);
CREATE INDEX `idx_orders_user_id` ON `orders` (`user_id`);
CREATE INDEX `idx_orders_status_created` ON `orders` (`status`, `created_at`);
CREATE INDEX `idx_orders_voucher_id` ON `orders` (`voucher_id`);
CREATE UNIQUE INDEX `uk_order_items_order_product` ON `order_items` (`order_id`, `product_id`);
CREATE INDEX `idx_order_items_order_id` ON `order_items` (`order_id`);
CREATE INDEX `idx_order_items_product_id` ON `order_items` (`product_id`);
CREATE UNIQUE INDEX `uk_payments_order_id` ON `payments` (`order_id`);
CREATE INDEX `idx_payments_status` ON `payments` (`status`);
CREATE UNIQUE INDEX `uk_voucher_usage_voucher_user` ON `voucher_usage` (`voucher_id`, `user_id`);
CREATE UNIQUE INDEX `uk_voucher_usage_order_id` ON `voucher_usage` (`order_id`);
CREATE INDEX `idx_voucher_usage_voucher_id` ON `voucher_usage` (`voucher_id`);
CREATE INDEX `idx_voucher_usage_user_id` ON `voucher_usage` (`user_id`);
CREATE UNIQUE INDEX `uk_reviews_user_product` ON `reviews` (`user_id`, `product_id`);
CREATE INDEX `idx_reviews_product_created` ON `reviews` (`product_id`, `created_at`);
CREATE INDEX `idx_product_views_user_time` ON `product_views` (`user_id`, `viewed_at`);
CREATE INDEX `idx_product_views_product_time` ON `product_views` (`product_id`, `viewed_at`);
CREATE INDEX `idx_borrows_user_id` ON `borrows` (`user_id`);
CREATE INDEX `idx_borrows_product_id` ON `borrows` (`product_id`);
CREATE INDEX `idx_borrows_status` ON `borrows` (`status`);
CREATE INDEX `idx_borrows_due_date` ON `borrows` (`due_date`);

-- Foreign Keys
ALTER TABLE `products` ADD FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`);
ALTER TABLE `product_images` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `product_discounts` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `carts` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `cart_items` ADD FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`);
ALTER TABLE `cart_items` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `orders` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `orders` ADD FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`);
ALTER TABLE `order_items` ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
ALTER TABLE `order_items` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `payments` ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
ALTER TABLE `voucher_usage` ADD FOREIGN KEY (`voucher_id`) REFERENCES `vouchers` (`id`);
ALTER TABLE `voucher_usage` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `voucher_usage` ADD FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`);
ALTER TABLE `reviews` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `reviews` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `product_views` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `product_views` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);
ALTER TABLE `borrows` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);
ALTER TABLE `borrows` ADD FOREIGN KEY (`product_id`) REFERENCES `products` (`id`);

-- ========== SAMPLE DATA ==========

-- Insert sample users
INSERT INTO users (name, email, password_hash, role, phone, address, created_at, updated_at) VALUES
('Admin User', 'admin@library.com', 'admin123', 'admin', '0123456789', 'Hà Nội', NOW(), NOW()),
('Trần Thị B', 'customer@library.com', 'customer123', 'customer', '0987654321', 'Hồ Chí Minh', NOW(), NOW()),
('Nguyễn Văn A', 'nguyena@library.com', 'password123', 'customer', '0918765432', 'Đà Nẵng', NOW(), NOW());

-- Insert sample categories
INSERT INTO categories (name, description, created_at, updated_at) VALUES
('Tiểu thuyết', 'Các tác phẩm tiểu thuyết hay lý thuyết', NOW(), NOW()),
('Khoa học', 'Sách về khoa học, vật lý, hóa học', NOW(), NOW()),
('Lịch sử', 'Sách về lịch sử các nước và thế giới', NOW(), NOW()),
('Công nghệ', 'Sách về công nghệ thông tin và lập trình', NOW(), NOW()),
('Văn học', 'Sách về văn học, thơ, truyện', NOW(), NOW());

-- Insert sample products
INSERT INTO products (name, description, original_price, price, stock, material, category_id, created_at, updated_at) VALUES
('Lập trình Java cơ bản', 'Hướng dẫn lập trình Java từ cơ bản đến nâng cao. Học các khái niệm OOP, Collections, Thread, Exception handling và nhiều hơn nữa.', 250000.00, 199000.00, 50, 'David Flanagan', 4, NOW(), NOW()),
('Lịch sử Việt Nam', 'Tổng quan lịch sử Việt Nam qua các thời kỳ từ thời cổ xưa đến hiện đại. Chi tiết về các sự kiện lịch sử quan trọng.', 300000.00, 250000.00, 30, 'Trần Trọng Tùng', 3, NOW(), NOW()),
('Tim hiểu Python', 'Sách học Python dành cho người mới bắt đầu. Từ cơ bản đến ứng dụng thực tế trong data science và web development.', 200000.00, 150000.00, 40, 'Bill Lubanovic', 4, NOW(), NOW()),
('Những người khiến thế giới thay đổi', 'Tiểu thuyết về những nhân vật lịch sử vĩ đại và những quyết định thay đổi vận mệnh thế giới.', 280000.00, 199000.00, 25, 'Karen Armstrong', 1, NOW(), NOW()),
('Cosmology và Vũ trụ học', 'Cuốn sách khoa học nổi tiếng về nguồn gốc và cấu trúc của vũ trụ. Dành cho những ai yêu thích thiên văn học.', 350000.00, 280000.00, 15, 'Carl Sagan', 2, NOW(), NOW()),
('C++ Programming', 'Hướng dẫn lập trình C++ cho những lập trình viên muốn thành thạo ngôn ngữ mạnh mẽ này.', 320000.00, 249000.00, 35, 'Bjarne Stroustrup', 4, NOW(), NOW()),
('Truyện tranh: One Piece', 'Bộ truyện tranh nổi tiếng của Eiichiro Oda. Kể về cuộc phiêu lưu tìm kho báu của Luffy.', 150000.00, 99000.00, 60, 'Eiichiro Oda', 5, NOW(), NOW()),
('Thế giới trong 100 năm tới', 'Dự đoán về tương lai của loài người, công nghệ và xã hội trong thế kỷ 21.', 220000.00, 169000.00, 20, 'Michio Kaku', 2, NOW(), NOW());

-- Insert sample reviews
INSERT INTO reviews (user_id, product_id, rating, comment, created_at) VALUES
(2, 1, 5, 'Sách rất hay và dễ hiểu! Tác giả giải thích rất rõ ràng các khái niệm Java.', NOW()),
(3, 1, 4, 'Nội dung tốt nhưng một số phần hơi nâng cao. Nên có tái bản cập nhật.', NOW()),
(2, 3, 5, 'Python là ngôn ngữ tuyệt vời! Cuốn sách này giúp tôi hiểu rõ hơn.', NOW()),
(3, 2, 4, 'Lịch sử Việt Nam được trình bày rất công phu và đầy đủ.', NOW()),
(2, 5, 5, 'Một trong những cuốn sách về vũ trụ hay nhất mà tôi từng đọc!', NOW());

-- Create sample carts
INSERT INTO carts (user_id, created_at, updated_at) VALUES
(2, NOW(), NOW()),
(3, NOW(), NOW());
