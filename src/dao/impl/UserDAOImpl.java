package dao.impl;

import dao.UserDAO;
import model.User;
import util.DBConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {

    @Override
    public User login(String username, String password) {

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User findByUsername(String username) {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updatePassword(String username, String newPassword) {

        String sql = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, username);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setSecurityQuestion(rs.getString("security_question"));
        user.setSecurityAnswer(rs.getString("security_answer"));
        user.setCreatedAt(rs.getTimestamp("created_at"));

        return user;
    }


    @Override
    public boolean registerUser(User user) {

        String sql = """
        INSERT INTO users
        (full_name, username, password, security_question, security_answer)
        VALUES (?, ?, ?, ?, ?)
    """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getSecurityQuestion());
            stmt.setString(5, user.getSecurityAnswer());

            return stmt.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}