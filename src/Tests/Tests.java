package Tests;

import util.DBConnection;

import java.sql.Connection;

public class Tests {
    public static void main(String[] args) {

        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("🎉 SUCCESS: Database is connected!");
        } else {
            System.out.println("❌ FAILED: No connection");
        }
    }
}