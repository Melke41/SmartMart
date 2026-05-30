package smartmart.service;

import smartmart.dao.UserDAO;
import smartmart.exception.SmartMartException;
import smartmart.exception.UnauthorizedAccessException;
import smartmart.model.Role;
import smartmart.model.User;

import java.sql.SQLException;

public class AuthService {
    private static User currentUser = null;
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public User login(String username, String password) throws SmartMartException {
        try {
            User user = userDAO.login(username, password);
            if (user == null) {
                throw new SmartMartException("Invalid username or password.");
            }
            currentUser = user;
            return currentUser;
        } catch (SQLException e) {
            throw new SmartMartException("Database error during login: " + e.getMessage(), e);
        }
    }

    public void logout() {
        currentUser = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void requireRole(Role... allowedRoles) throws UnauthorizedAccessException {
        if (currentUser == null) {
            throw new UnauthorizedAccessException("Guest", "access this module");
        }
        for (Role allowedRole : allowedRoles) {
            if (currentUser.getRole() == allowedRole) {
                return;
            }
        }
        throw new UnauthorizedAccessException(currentUser.getUsername(), "access this module");
    }
}
