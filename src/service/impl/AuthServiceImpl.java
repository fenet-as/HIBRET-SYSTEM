package service.impl;

import dao.UserDAO;
import dao.impl.UserDAOImpl;
import model.User;
import service.AuthService;

public class AuthServiceImpl implements AuthService {

    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public User login(String username, String password) {
        return userDAO.login(username, password);
    }

    @Override
    public User findUser(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public void resetPassword(String username, String newPassword) {
        userDAO.updatePassword(username, newPassword);
    }

    @Override
    public boolean register(User user) {
        return userDAO.registerUser(user);
    }
}