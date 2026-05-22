package dao;

import model.User;

public interface UserDAO {

    User login(String username, String password);
}