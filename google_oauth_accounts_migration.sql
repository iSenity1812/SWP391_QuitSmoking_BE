-- Migration script for Google OAuth Accounts table
-- Run this script in your PostgreSQL database

CREATE TABLE IF NOT EXISTS google_oauth_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    google_id VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    given_name VARCHAR(100),
    family_name VARCHAR(100),
    picture_url VARCHAR(500),
    locale VARCHAR(10),
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP,
    user_id UUID NOT NULL,
    
    CONSTRAINT fk_google_oauth_user 
        FOREIGN KEY (user_id) 
        REFERENCES users("UserID") 
        ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_google_oauth_google_id ON google_oauth_accounts(google_id);
CREATE INDEX IF NOT EXISTS idx_google_oauth_email ON google_oauth_accounts(email);
CREATE INDEX IF NOT EXISTS idx_google_oauth_user_id ON google_oauth_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_google_oauth_active ON google_oauth_accounts(is_active);

-- Add comments for documentation
COMMENT ON TABLE google_oauth_accounts IS 'Stores Google OAuth account information linked to users';
COMMENT ON COLUMN google_oauth_accounts.google_id IS 'Google user ID from Google OAuth';
COMMENT ON COLUMN google_oauth_accounts.email IS 'Email address from Google account';
COMMENT ON COLUMN google_oauth_accounts.user_id IS 'Reference to the main user account'; 