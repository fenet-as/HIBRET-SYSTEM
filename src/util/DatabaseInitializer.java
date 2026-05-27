//package util;
//
//import java.sql.Connection;
//import java.sql.Statement;
//import java.sql.SQLException;
//
//public class DatabaseInitializer {
//
//    public static void init() {
//
//        try (Connection conn = DBConnection.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            // 1. USERS
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS users (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    full_name VARCHAR(100) NOT NULL,
//                    username VARCHAR(50) NOT NULL UNIQUE,
//                    password VARCHAR(100) NOT NULL,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//                )
//            """);
//
//            // 2. GROUPS
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS groups_table (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    name VARCHAR(100) NOT NULL,
//                    type ENUM('EQUb', 'EDIR') NOT NULL,
//                    created_by INT NOT NULL,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (created_by) REFERENCES users(id)
//                )
//            """);
//
//            // 3. MEMBERS
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS members (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    full_name VARCHAR(100) NOT NULL,
//                    phone VARCHAR(20),
//                    address VARCHAR(255),
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//                )
//            """);
//
//            // 4. GROUP MEMBERS
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS group_members (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    group_id INT NOT NULL,
//                    member_id INT NOT NULL,
//                    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (group_id) REFERENCES groups_table(id),
//                    FOREIGN KEY (member_id) REFERENCES members(id)
//                )
//            """);
//
//            // 5. TRANSACTIONS
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS transactions (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    group_id INT NOT NULL,
//                    member_id INT,
//                    type ENUM('CONTRIBUTION', 'PAYOUT', 'EMERGENCY') NOT NULL,
//                    amount DECIMAL(10,2) NOT NULL,
//                    note TEXT,
//                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
//                    FOREIGN KEY (group_id) REFERENCES groups_table(id),
//                    FOREIGN KEY (member_id) REFERENCES members(id)
//                )
//            """);
//
//            // 6. EQUb ROTATION
//            stmt.executeUpdate("""
//                CREATE TABLE IF NOT EXISTS equb_rotation (
//                    id INT PRIMARY KEY AUTO_INCREMENT,
//                    group_id INT NOT NULL,
//                    member_id INT NOT NULL,
//                    cycle_number INT NOT NULL,
//                    is_paid BOOLEAN DEFAULT FALSE,
//                    FOREIGN KEY (group_id) REFERENCES groups_table(id),
//                    FOREIGN KEY (member_id) REFERENCES members(id)
//                )
//            """);
//
//            System.out.println("✅ All tables created successfully!");
//
//        } catch (SQLException e) {
//            System.out.println("❌ Table creation failed!");
//            e.printStackTrace();
//        }
//    }
//}



package util;

import util.DBConnection;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initializeDatabase() {

        try {

            Connection conn = DBConnection.getConnection();

            Statement stmt = conn.createStatement();

            // USERS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id SERIAL PRIMARY KEY,
                    full_name VARCHAR(100) NOT NULL,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    security_question VARCHAR(255),
                    security_answer VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // GROUPS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS groups (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    type VARCHAR(20) NOT NULL,
                    created_by INT REFERENCES users(id),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // MEMBERS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS members (
                    id SERIAL PRIMARY KEY,
                    full_name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20),
                    address VARCHAR(255),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // GROUP MEMBERS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS group_members (
                    id SERIAL PRIMARY KEY,
                    group_id INT REFERENCES groups(id),
                    member_id INT REFERENCES members(id),
                    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // TRANSACTIONS
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS transactions (
                    id SERIAL PRIMARY KEY,
                    group_id INT REFERENCES groups(id),
                    member_id INT REFERENCES members(id),
                    type VARCHAR(30) NOT NULL,
                    amount DECIMAL(10,2) NOT NULL,
                    note TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // EQUb ROTATION
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS equb_rotation (
                    id SERIAL PRIMARY KEY,
                    group_id INT REFERENCES groups(id),
                    member_id INT REFERENCES members(id),
                    cycle_number INT NOT NULL,
                    is_paid BOOLEAN DEFAULT FALSE
                )
            """);

            System.out.println("✅ ALL TABLES CREATED SUCCESSFULLY!");

        } catch (Exception e) {

            System.out.println("❌ TABLE CREATION FAILED!");
            e.printStackTrace();
        }
    }
}