package service;

import model.User;

public interface AuthService {

    User login(String username, String password);

    User findUser(String username);

    void resetPassword(String username, String newPassword);

    boolean register(User user);
}