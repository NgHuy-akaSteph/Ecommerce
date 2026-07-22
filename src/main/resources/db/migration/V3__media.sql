-- =============================================
-- V4: Media Management — Upload, Process, Export
-- =============================================

-- Bảng media: lưu metadata tất cả các loại file
CREATE TABLE media (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    storage_key     VARCHAR(500) NOT NULL UNIQUE,
    public_url      VARCHAR(1000),
    original_name   VARCHAR(255) NOT NULL,
    mime_type       VARCHAR(100) NOT NULL,
    file_size       BIGINT NOT NULL,
    media_type      VARCHAR(50) NOT NULL,   -- 'IMAGE' | 'DOCUMENT'
    width           INTEGER,
    height          INTEGER,
    bucket_type     VARCHAR(50) NOT NULL,   -- 'PUBLIC' | 'PRIVATE'
    uploaded_by     UUID REFERENCES users(id) ON DELETE SET NULL,
    status          VARCHAR(50) DEFAULT 'ACTIVE',  -- 'ACTIVE' | 'DELETED'
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    CONSTRAINT chk_media_type CHECK (media_type IN ('IMAGE', 'DOCUMENT')),
    CONSTRAINT chk_bucket_type CHECK (bucket_type IN ('PUBLIC', 'PRIVATE')),
    CONSTRAINT chk_media_status CHECK (status IN ('ACTIVE', 'DELETED'))
);

-- Bảng media_attachments: liên kết media với entity (product, user, order)
CREATE TABLE media_attachments (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    media_id        UUID NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    entity_type     VARCHAR(100) NOT NULL,  -- 'PRODUCT' | 'USER' | 'ORDER' | 'REPORT'
    entity_id       UUID NOT NULL,
    attachment_type VARCHAR(50),  -- 'PRODUCT_IMAGE' | 'PRODUCT_THUMBNAIL' | 'USER_AVATAR' | 'ORDER_INVOICE'
    sort_order      INTEGER DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    CONSTRAINT chk_attachment_type CHECK (attachment_type IN (
        'PRODUCT_IMAGE', 'PRODUCT_THUMBNAIL', 'USER_AVATAR',
        'ORDER_INVOICE', 'ORDER_RECEIPT', 'EXPORT_REPORT'
    ))
);

-- Bảng export_jobs: async export (PDF/Excel)
CREATE TABLE export_jobs (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_type        VARCHAR(50) NOT NULL,   -- 'ORDER_REPORT' | 'PRODUCT_REPORT' | 'REVENUE_REPORT'
    status          VARCHAR(50) DEFAULT 'PENDING',  -- 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED'
    format          VARCHAR(20) NOT NULL,   -- 'PDF' | 'EXCEL' | 'CSV'
    filters         JSONB,
    file_path       VARCHAR(1000),
    error_message   TEXT,
    requested_by    UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    completed_at    TIMESTAMP WITH TIME ZONE,

    CONSTRAINT chk_job_type CHECK (job_type IN ('ORDER_REPORT', 'PRODUCT_REPORT', 'REVENUE_REPORT')),
    CONSTRAINT chk_export_format CHECK (format IN ('PDF', 'EXCEL', 'CSV')),
    CONSTRAINT chk_job_status CHECK (status IN ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED'))
);

-- Indexes cho media
-- Composite (entity_type, entity_id) — query phổ biến nhất: "lấy tất cả media của 1 product"
CREATE INDEX IF NOT EXISTS idx_media_attachments_entity ON media_attachments(entity_type, entity_id);
-- Partial index cho active media — phổ biến hơn DELETED
CREATE INDEX IF NOT EXISTS idx_media_status_active ON media(status) WHERE status = 'ACTIVE';
-- Index cho export jobs lookup
CREATE INDEX IF NOT EXISTS idx_export_jobs_status ON export_jobs(status);
CREATE INDEX IF NOT EXISTS idx_export_jobs_requested_by ON export_jobs(requested_by);
-- Index cho media uploaded_by
CREATE INDEX IF NOT EXISTS idx_media_uploaded_by ON media(uploaded_by);
