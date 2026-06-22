-- 1. users
CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255),
    role VARCHAR(20) NOT NULL CHECK (role IN ('CUSTOMER', 'ADMIN')),
    is_active BOOLEAN DEFAULT false NOT NULL,
    is_email_verified BOOLEAN DEFAULT false NOT NULL,
    display_name VARCHAR(120),
    version INTEGER DEFAULT 0 NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);

-- 2. refresh_tokens
CREATE TABLE refresh_tokens (
    refresh_token_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id),
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked BOOLEAN DEFAULT false NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 3. email_verifications
CREATE TABLE email_verifications (
    verification_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- 4. login_attempts
CREATE TABLE login_attempts (
    attempt_id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    attempt_time TIMESTAMPTZ DEFAULT now(),
    success BOOLEAN DEFAULT false,
    failure_reason VARCHAR(100),
    user_id BIGINT
);

-- Indexes
CREATE INDEX idx_login_attempts_email_time ON login_attempts(email, attempt_time);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Seed admin user (password set at startup by DataInitializer)
INSERT INTO users (email, password_hash, role, is_active, is_email_verified, display_name)
VALUES ('admin@example.com', 'PLACEHOLDER_WILL_BE_REPLACED', 'ADMIN', true, true, 'Admin');
