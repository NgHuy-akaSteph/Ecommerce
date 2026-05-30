-- =============================================
-- V3: Add version column for optimistic locking
-- =============================================

ALTER TABLE roles ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE permissions ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE users ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE categories ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE tags ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE products ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE carts ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE cart_details ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE orders ADD COLUMN version BIGINT DEFAULT 0;
ALTER TABLE order_details ADD COLUMN version BIGINT DEFAULT 0;

