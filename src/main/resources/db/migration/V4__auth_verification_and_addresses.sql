-- =============================================
-- V4: Auth Verification & Address Tables
-- Tách riêng email_verifications (token-based) và addresses (multi-address).
-- =============================================

-- =============================================
-- Email Verifications
-- Bảng riêng để track mọi token gửi qua email.
-- Hỗ trợ nhiều loại: EMAIL_VERIFY, PASSWORD_RESET.
-- =============================================
CREATE TABLE email_verifications (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email           VARCHAR(255) NOT NULL,
    token           VARCHAR(255) NOT NULL UNIQUE,
    type            VARCHAR(50) NOT NULL,   -- 'EMAIL_VERIFY' | 'PASSWORD_RESET'
    status          VARCHAR(50) NOT NULL DEFAULT 'PENDING',  -- 'PENDING' | 'VERIFIED' | 'EXPIRED' | 'USED'
    expires_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at     TIMESTAMP WITH TIME ZONE,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE,
    version     BIGINT NOT NULL DEFAULT 0,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_by      VARCHAR(255) DEFAULT '',
    updated_by      VARCHAR(255) DEFAULT '',

    CONSTRAINT chk_verification_type CHECK (type IN ('EMAIL_VERIFY', 'PASSWORD_RESET')),
    CONSTRAINT chk_verification_status CHECK (status IN ('PENDING', 'VERIFIED', 'EXPIRED', 'USED'))
);

CREATE INDEX IF NOT EXISTS idx_email_verifications_token ON email_verifications(token);
CREATE INDEX IF NOT EXISTS idx_email_verifications_user_id ON email_verifications(user_id);

-- Index for cleanup: tìm các token đã hết hạn để mark EXPIRED (cronjob)
CREATE INDEX IF NOT EXISTS idx_email_verifications_expires_pending
    ON email_verifications(expires_at) WHERE status = 'PENDING';

-- =============================================
-- Addresses (multi-address per user)
-- Thay thế cột `address TEXT` trên bảng users.
-- Mỗi user có thể có nhiều địa chỉ và đặt 1 cái làm mặc định (isDefault).
-- =============================================
CREATE TABLE addresses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    full_name   VARCHAR(255),
    phone       VARCHAR(20),
    street      VARCHAR(500),
    ward        VARCHAR(255),
    district    VARCHAR(255),
    city        VARCHAR(255),
    postal_code VARCHAR(20),
    country     VARCHAR(100) DEFAULT 'Vietnam',
    label       VARCHAR(50),   -- 'HOME' | 'WORK' | 'OTHER'
    note        TEXT,
    is_default  BOOLEAN NOT NULL DEFAULT FALSE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    version     BIGINT NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at  TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    created_by  VARCHAR(255) DEFAULT '',
    updated_by  VARCHAR(255) DEFAULT '',

    CONSTRAINT chk_address_label CHECK (label IS NULL OR label IN ('HOME', 'WORK', 'OTHER'))
);

CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);
CREATE INDEX IF NOT EXISTS idx_addresses_deleted ON addresses(deleted);

-- Mỗi user chỉ có tối đa 1 địa chỉ mặc định (active, chưa xoá mềm)
CREATE UNIQUE INDEX IF NOT EXISTS uq_addresses_one_default_per_user
    ON addresses(user_id) WHERE is_default = TRUE AND deleted = FALSE;
