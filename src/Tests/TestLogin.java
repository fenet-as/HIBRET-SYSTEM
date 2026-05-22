package Tests;

import model.User;
import service.AuthService;
import service.impl.AuthServiceImpl;

public class TestLogin {

    public static void main(String[] args) {

        AuthService authService = new AuthServiceImpl();

        User user = authService.login("admin", "1234");

        if (user != null) {

            System.out.println("✅ Login Successful");
            System.out.println("Welcome " + user.getFullName());

        } else {

            System.out.println("❌ Invalid username or password");
        }
    }
}