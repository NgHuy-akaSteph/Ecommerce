-- =============================================
-- V1: Core Schema for Ecommerce Application
-- Database: PostgreSQL
-- =============================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Roles
CREATE TABLE roles (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE,
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(255) DEFAULT '',
    updated_by  VARCHAR(255) DEFAULT ''
);

-- Permissions
CREATE TABLE permissions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255) NOT NULL,
    api_path    VARCHAR(500) NOT NULL,
    method      VARCHAR(10) NOT NULL,
    module      VARCHAR(100) NOT NULL,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE,
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(255) DEFAULT '',
    updated_by  VARCHAR(255) DEFAULT ''
);

-- Permission-Role (Many-to-Many)
CREATE TABLE permission_role (
    role_id       UUID NOT NULL REFERENCES roles(id),
    permission_id UUID NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- Users
CREATE TABLE users (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username      VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255),
    name          VARCHAR(255),
    refresh_token TEXT,
    role_id       UUID REFERENCES roles(id),
    email         VARCHAR(255) UNIQUE,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone         VARCHAR(20),
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    avatar_url    VARCHAR(500),
    reset_password_token VARCHAR(255),
    reset_password_token_expires_at TIMESTAMP WITH TIME ZONE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    locked_until  TIMESTAMP WITH TIME ZONE,
    deleted       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE,
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(255) DEFAULT '',
    updated_by    VARCHAR(255) DEFAULT ''
);

-- Categories
CREATE TABLE categories (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL UNIQUE,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Tags
CREATE TABLE tags (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(255),
    description VARCHAR(500),
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE,
    updated_at  TIMESTAMP WITH TIME ZONE,
    created_by  VARCHAR(255) DEFAULT '',
    updated_by  VARCHAR(255) DEFAULT ''
);

-- Products
CREATE TABLE products (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(255),
    short_description TEXT,
    thumbnail         VARCHAR(1000),
    price             NUMERIC(19,2) NOT NULL DEFAULT 0,
    discount          NUMERIC(19,2) NOT NULL DEFAULT 0,
    category_id       UUID REFERENCES categories(id),
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP WITH TIME ZONE,
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(255) DEFAULT '',
    updated_by        VARCHAR(255) DEFAULT ''
);

-- Product sliders (ElementCollection)
CREATE TABLE products_sliders (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sliders    VARCHAR(1000)
);

-- Product-Tag (Many-to-Many)
CREATE TABLE product_tags (
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag_id     UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, tag_id)
);

-- Carts
CREATE TABLE carts (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sum        INTEGER NOT NULL DEFAULT 0,
    user_id    UUID REFERENCES users(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Cart Details
CREATE TABLE cart_details (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quantity   BIGINT NOT NULL DEFAULT 0,
    price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    cart_id    UUID REFERENCES carts(id),
    product_id UUID REFERENCES products(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Orders
CREATE TABLE orders (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    customer_name    VARCHAR(255),
    customer_address TEXT,
    customer_phone   VARCHAR(20),
    status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    user_id          UUID REFERENCES users(id),
    deleted          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP WITH TIME ZONE,
    updated_at       TIMESTAMP WITH TIME ZONE,
    created_by       VARCHAR(255) DEFAULT '',
    updated_by       VARCHAR(255) DEFAULT ''
);

-- Order Details
CREATE TABLE order_details (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quantity   BIGINT NOT NULL DEFAULT 0,
    price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    order_id   UUID REFERENCES orders(id),
    product_id UUID REFERENCES products(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- =============================================
-- V1 continued: CHECK constraints
-- =============================================

-- Ensure cart_details.quantity is always positive
ALTER TABLE cart_details ADD CONSTRAINT chk_cart_details_quantity_positive CHECK (quantity > 0);

-- Ensure orders.status is a valid value
ALTER TABLE orders ADD CONSTRAINT chk_orders_status
    CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED'));

-- Ensure products.discount is between 0 and 100 (percentage)
ALTER TABLE products ADD CONSTRAINT chk_products_discount_range
    CHECK (discount >= 0 AND discount <= 100);

-- Ensure carts.sum is non-negative
ALTER TABLE carts ADD CONSTRAINT chk_carts_sum_non_negative CHECK (sum >= 0);

-- Unique constraint on carts.user_id (one cart per user)
ALTER TABLE carts ADD CONSTRAINT uk_carts_user_id UNIQUE (user_id);

-- Unique constraint on permissions(api_path, method)
ALTER TABLE permissions ADD CONSTRAINT uk_permissions_api_path_method UNIQUE (api_path, method);

-- =============================================
-- V1 continued: Indexes
-- =============================================

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_details_order_id ON order_details(order_id);
CREATE INDEX idx_order_details_product_id ON order_details(product_id);
CREATE INDEX idx_cart_details_cart_id ON cart_details(cart_id);
CREATE INDEX idx_carts_user_id ON carts(user_id);

-- =============================================
-- V1 continued: Optimistic locking (version column)
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

-- =============================================
-- V1 continued: Partial indexes for nullable auth columns
-- =============================================

CREATE INDEX idx_users_email ON users(email) WHERE email IS NOT NULL;
CREATE INDEX idx_users_phone ON users(phone) WHERE phone IS NOT NULL;
CREATE INDEX idx_users_reset_password_token ON users(reset_password_token) WHERE reset_password_token IS NOT NULL;
CREATE INDEX idx_users_locked_until ON users(locked_until) WHERE locked_until IS NOT NULL;
