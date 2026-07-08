-- =============================================
-- V3: Product Variants
-- Mặc định: MỌI sản phẩm đều có variant.
-- Nguồn tồn kho duy nhất: product_variants.quantity.
-- products.quantity đã bị loại bỏ từ V1.
-- =============================================

-- (1) variant_options: các loại option (Size, Color, Material...)
CREATE TABLE variant_options (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(100) NOT NULL,
    code        VARCHAR(50) NOT NULL UNIQUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    version     BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_by  VARCHAR(255) DEFAULT '',
    updated_by  VARCHAR(255) DEFAULT ''
);

-- (2) variant_values: giá trị của từng option (S, M, L, Red, Blue...)
CREATE TABLE variant_values (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    option_id       UUID NOT NULL REFERENCES variant_options(id),
    value           VARCHAR(100) NOT NULL,
    code            VARCHAR(50),
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    version         BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_by      VARCHAR(255) DEFAULT '',
    updated_by      VARCHAR(255) DEFAULT '',
    CONSTRAINT uk_variant_values_option_value UNIQUE (option_id, value)
);

-- (3) product_variants: biến thể cụ thể của 1 sản phẩm
CREATE TABLE product_variants (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id       UUID NOT NULL REFERENCES products(id),
    sku              VARCHAR(100) NOT NULL,
    price            NUMERIC(19,2) NOT NULL DEFAULT 0,
    compare_at_price NUMERIC(19,2),
    quantity         BIGINT NOT NULL DEFAULT 0,
    deleted          BOOLEAN NOT NULL DEFAULT FALSE,
    version          BIGINT NOT NULL DEFAULT 0,
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at       TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_by       VARCHAR(255) DEFAULT '',
    updated_by       VARCHAR(255) DEFAULT '',

    CONSTRAINT chk_product_variants_price_nonneg     CHECK (price >= 0),
    CONSTRAINT chk_product_variants_quantity_nonneg  CHECK (quantity >= 0),
    CONSTRAINT chk_product_variants_compare_at       CHECK (compare_at_price IS NULL OR compare_at_price >= 0)
);

-- SKU chỉ unique với bản ghi chưa xoá mềm (tránh đụng khi soft-delete rồi tạo lại)
CREATE UNIQUE INDEX IF NOT EXISTS uq_product_variants_sku_active
    ON product_variants(sku) WHERE deleted = FALSE;

-- (4) variant_value_pivot: N-N giữa variant và value (1 variant có nhiều option values)
CREATE TABLE variant_value_pivot (
    variant_id   UUID NOT NULL REFERENCES product_variants(id) ON DELETE CASCADE,
    value_id     UUID NOT NULL REFERENCES variant_values(id),
    PRIMARY KEY (variant_id, value_id)
);

-- (5) Cart Detail → Variant Link
ALTER TABLE cart_details ADD COLUMN IF NOT EXISTS variant_id UUID REFERENCES product_variants(id);

-- (6) Order Detail → Variant Link
ALTER TABLE order_details ADD COLUMN IF NOT EXISTS variant_id UUID REFERENCES product_variants(id);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_variant_options_name             ON variant_options(name);
CREATE INDEX IF NOT EXISTS idx_variant_values_option            ON variant_values(option_id);
CREATE INDEX IF NOT EXISTS idx_variant_values_deleted           ON variant_values(deleted);
CREATE INDEX IF NOT EXISTS idx_product_variants_product        ON product_variants(product_id);
CREATE INDEX IF NOT EXISTS idx_product_variants_sku             ON product_variants(sku);
CREATE INDEX IF NOT EXISTS idx_product_variants_deleted         ON product_variants(deleted);
CREATE INDEX IF NOT EXISTS idx_product_variants_product_active  ON product_variants(product_id) WHERE deleted = FALSE;
