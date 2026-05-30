-- =============================================
-- V1: Initial Schema for Ecommerce Application
-- Database: PostgreSQL
-- =============================================

-- Roles
CREATE TABLE roles (
    id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
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
    id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
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
    role_id       VARCHAR(36) NOT NULL REFERENCES roles(id),
    permission_id VARCHAR(36) NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

-- Users
CREATE TABLE users (
    id            VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255),
    name          VARCHAR(255),
    address       TEXT,
    refresh_token TEXT,
    role_id       VARCHAR(36) REFERENCES roles(id),
    deleted       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP WITH TIME ZONE,
    updated_at    TIMESTAMP WITH TIME ZONE,
    created_by    VARCHAR(255) DEFAULT '',
    updated_by    VARCHAR(255) DEFAULT ''
);

-- Categories
CREATE TABLE categories (
    id         VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    name       VARCHAR(255) NOT NULL UNIQUE,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Tags
CREATE TABLE tags (
    id          VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
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
    id                VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    name              VARCHAR(255),
    short_description TEXT,
    thumbnail         VARCHAR(1000),
    price             NUMERIC(19,2) NOT NULL DEFAULT 0,
    quantity          BIGINT NOT NULL DEFAULT 0,
    discount          NUMERIC(19,2) NOT NULL DEFAULT 0,
    category_id       VARCHAR(36) REFERENCES categories(id),
    deleted           BOOLEAN NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP WITH TIME ZONE,
    updated_at        TIMESTAMP WITH TIME ZONE,
    created_by        VARCHAR(255) DEFAULT '',
    updated_by        VARCHAR(255) DEFAULT ''
);

-- Product sliders (ElementCollection)
CREATE TABLE products_sliders (
    product_id VARCHAR(36) NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sliders    VARCHAR(1000)
);

-- Product-Tag (Many-to-Many)
CREATE TABLE product_tags (
    product_id VARCHAR(36) NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    tag_id     VARCHAR(36) NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, tag_id)
);

-- Carts
CREATE TABLE carts (
    id         VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    sum        INTEGER NOT NULL DEFAULT 0,
    user_id    VARCHAR(36) REFERENCES users(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Cart Details
CREATE TABLE cart_details (
    id         VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    quantity   BIGINT NOT NULL DEFAULT 0,
    price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    cart_id    VARCHAR(36) REFERENCES carts(id),
    product_id VARCHAR(36) REFERENCES products(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Orders
CREATE TABLE orders (
    id               VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    total_price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    customer_name    VARCHAR(255),
    customer_address TEXT,
    customer_phone   VARCHAR(20),
    status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    user_id          VARCHAR(36) REFERENCES users(id),
    deleted          BOOLEAN NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP WITH TIME ZONE,
    updated_at       TIMESTAMP WITH TIME ZONE,
    created_by       VARCHAR(255) DEFAULT '',
    updated_by       VARCHAR(255) DEFAULT ''
);

-- Order Details
CREATE TABLE order_details (
    id         VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::VARCHAR,
    quantity   BIGINT NOT NULL DEFAULT 0,
    price      NUMERIC(19,2) NOT NULL DEFAULT 0,
    order_id   VARCHAR(36) REFERENCES orders(id),
    product_id VARCHAR(36) REFERENCES products(id),
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE,
    created_by VARCHAR(255) DEFAULT '',
    updated_by VARCHAR(255) DEFAULT ''
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_details_order_id ON order_details(order_id);
CREATE INDEX idx_order_details_product_id ON order_details(product_id);
CREATE INDEX idx_cart_details_cart_id ON cart_details(cart_id);
CREATE INDEX idx_carts_user_id ON carts(user_id);
