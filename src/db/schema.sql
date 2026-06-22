-- ==========================================
-- 1. DROP EXISTING TABLES (For clean resets)
-- ==========================================
DROP TABLE IF EXISTS emergency_cases CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS group_members CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==========================================
-- 2. CREATE SYSTEM TABLES
-- ==========================================

-- Users Table (Maps to model.User)
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       full_name VARCHAR(100),
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       security_question VARCHAR(255),
                       security_answer VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Members Table (Maps to model.Member)
CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         full_name VARCHAR(100) NOT NULL,
                         phone VARCHAR(20) UNIQUE NOT NULL,
                         email VARCHAR(100),
                         status VARCHAR(20) DEFAULT 'Active'
);

-- Groups Table (Maps to model.Group)
CREATE TABLE groups (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        type VARCHAR(10) NOT NULL, -- 'EQUB' or 'EDIR'
                        created_by INT REFERENCES users(id) ON DELETE SET NULL,
                        contribution_amount NUMERIC(12, 2) NOT NULL,
                        fund_balance NUMERIC(12, 2) DEFAULT 0.00,
                        rules TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Group Members Map (Many-to-Many bridge)
CREATE TABLE group_members (
                               group_id INT REFERENCES groups(id) ON DELETE CASCADE,
                               member_id INT REFERENCES members(id) ON DELETE CASCADE,
                               PRIMARY KEY (group_id, member_id)
);

-- Transactions Table (Maps to model.Transaction)
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              member_id INT REFERENCES members(id) ON DELETE CASCADE,
                              group_id INT REFERENCES groups(id) ON DELETE CASCADE,
                              amount NUMERIC(12, 2) NOT NULL,
                              type VARCHAR(20) NOT NULL, -- 'CONTRIBUTION' or 'PAYOUT'
                              description VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Emergency Cases Table (Maps to model.EmergencyCase)
CREATE TABLE emergency_cases (
                                 id SERIAL PRIMARY KEY,
                                 member_id INT REFERENCES members(id) ON DELETE CASCADE,
                                 group_id INT REFERENCES groups(id) ON DELETE CASCADE,
                                 emergency_type VARCHAR(50) NOT NULL,
                                 amount_needed NUMERIC(12, 2) NOT NULL,
                                 description TEXT,
                                 status VARCHAR(20) DEFAULT 'Pending',
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

