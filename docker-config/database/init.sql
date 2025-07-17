-- ========================================
-- 📊 DATABASE INITIALIZATION SCRIPT
-- ========================================
-- This script runs when PostgreSQL container starts for the first time
-- It's used to create the initial database structure

-- Create additional schemas if needed
-- CREATE SCHEMA IF NOT EXISTS public;

-- Set default permissions
-- GRANT ALL PRIVILEGES ON SCHEMA public TO root;

-- You can add initial data here if needed
-- INSERT INTO users (username, email) VALUES ('admin', 'admin@example.com');

-- For now, we'll let Spring Boot JPA handle table creation via DDL
SELECT 'Database initialized successfully!' as message;
