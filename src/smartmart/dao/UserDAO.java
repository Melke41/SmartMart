package smartmart.dao;

import smartmart.model.Admin;
import smartmart.model.Cashier;
import smartmart.model.Manager;
import smartmart.model.Role;
import smartmart.model.User;
import smartmart.util.DatabaseConnection;
import smartmart.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User login(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND is_active = 1";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, PasswordUtil.hashPassword(password));
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String query = "SELECT * FROM users";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapUser(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    public boolean addUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, role, full_name, is_active) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword()));
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getFullName());
            ps.setInt(5, user.isActive() ? 1 : 0);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean updateUser(User user) throws SQLException {
        String query = "UPDATE users SET username = ?, password = ?, role = ?, full_name = ?, is_active = ? WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            
            String password = user.getPassword();
            if (password != null && password.length() != 64) {
                password = PasswordUtil.hashPassword(password);
            }
            ps.setString(2, password);
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getFullName());
            ps.setInt(5, user.isActive() ? 1 : 0);
            ps.setInt(6, user.getUserId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean deleteUser(int userId) throws SQLException {
        String query = "UPDATE users SET is_active = 0 WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        String fullName = rs.getString("full_name");
        boolean isActive = rs.getInt("is_active") == 1;

        Role role = Role.valueOf(roleStr);
        switch (role) {
            case ADMIN:
                return new Admin(id, username, password, role, fullName, isActive);
            case MANAGER:
                return new Manager(id, username, password, role, fullName, isActive);
            case CASHIER:
                return new Cashier(id, username, password, role, fullName, isActive);
            default:
                return null;
        }
    }
}
