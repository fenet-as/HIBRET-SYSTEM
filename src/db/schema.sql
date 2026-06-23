-- ==========================================
-- 🛠️ SYSTEM RESET: CLEAN DROP ACTIONS
-- ==========================================
DROP VIEW IF EXISTS user_member_group_summary CASCADE;
DROP TABLE IF EXISTS emergency_cases CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS group_members CASCADE;
DROP TABLE IF EXISTS equb_groups CASCADE;
DROP TABLE IF EXISTS edir_groups CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS members CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ==========================================
-- 1. USERS SECURITY & PROTOCOL DOMAIN
-- ==========================================
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       full_name VARCHAR(255),
                       username VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       security_question VARCHAR(255),
                       security_answer VARCHAR(255),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. REGISTRATION AND HEADCOUNT SYSTEM
-- ==========================================
CREATE TABLE members (
                         id SERIAL PRIMARY KEY,
                         full_name VARCHAR(255) NOT NULL,
                         phone VARCHAR(50) NOT NULL,
                         email VARCHAR(150),
                         status VARCHAR(50) DEFAULT 'ACTIVE'
);

-- ==========================================
-- 3. UNIFIED PARENT CLUSTER ENTITY (GROUPS)
-- ==========================================
CREATE TABLE groups (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(150) NOT NULL,
                        type VARCHAR(50) NOT NULL, -- Managed systematically via 'EDIR' or 'EQUB' values
                        created_by INTEGER REFERENCES users(id) ON DELETE SET NULL,
                        contribution_amount NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                        fund_balance NUMERIC(15, 2) DEFAULT 0.00,
                        rules TEXT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 4. SUBSIDIARY ATTRIBUTE METRICS FOR EDIR
-- ==========================================
CREATE TABLE edir_groups (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(150),
                             contribution_amount DOUBLE PRECISION DEFAULT 0.0,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 5. SUBSIDIARY ATTRIBUTE METRICS FOR EQUB
-- ==========================================
CREATE TABLE equb_groups (
                             id SERIAL PRIMARY KEY,
                             name VARCHAR(150),
                             contribution_amount DOUBLE PRECISION DEFAULT 0.0,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 6. MULTI-TO-MULTI JUNCTION OVERLAY
-- ==========================================
CREATE TABLE group_members (
                               group_id INTEGER REFERENCES groups(id) ON DELETE CASCADE,
                               member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
                               PRIMARY KEY (group_id, member_id)
);

-- ==========================================
-- 7. LEDGER MUTATION MONITORING (TRANSACTIONS)
-- ==========================================
CREATE TABLE transactions (
                              id SERIAL PRIMARY KEY,
                              member_id INTEGER REFERENCES members(id) ON DELETE SET NULL,
                              group_id INTEGER REFERENCES groups(id) ON DELETE CASCADE,
                              amount NUMERIC(15, 2) NOT NULL,
                              type VARCHAR(50) NOT NULL, -- e.g., 'CONTRIBUTION', 'PAYMENT', 'PAYOUT', 'REGISTRATION'
                              description VARCHAR(255),
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 8. RISK CRITERIA ALLOCATION (EMERGENCY CASES)
-- ==========================================
CREATE TABLE emergency_cases (
                                 id SERIAL PRIMARY KEY,
                                 member_id INTEGER REFERENCES members(id) ON DELETE CASCADE,
                                 group_id INTEGER REFERENCES groups(id) ON DELETE CASCADE,
                                 emergency_type VARCHAR(100) NOT NULL,
                                 amount_needed NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
                                 description TEXT,
                                 status VARCHAR(50) DEFAULT 'PENDING', -- e.g., 'PENDING', 'APPROVED', 'REJECTED'
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 9. DYNAMIC LOGICAL JOIN SUMMARY VIEW
-- ==========================================
-- Generates user_member_group_summary on the fly.
-- Ensures 100% data integrity with absolute zero data lag for the UI panels.
CREATE OR REPLACE VIEW user_member_group_summary AS
SELECT
    ROW_NUMBER() OVER () AS id, -- Satisfies explicit integer unique index lookup needs
    g.created_by AS creator_user_id,
    m.id AS member_id,
    g.id AS group_id,
    m.full_name AS member_name,
    g.name AS group_name,
    g.type AS group_type,
    g.created_at AS joined_at
FROM group_members gm
         JOIN groups g ON gm.group_id = g.id
         JOIN members m ON gm.member_id = m.id;