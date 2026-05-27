package dao;

import model.User;

public interface UserDAO {

    User login(String username, String password);

    User findByUsername(String username);

    void updatePassword(String username, String newPassword);
}