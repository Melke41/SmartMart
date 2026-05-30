package smartmart.dao;

import smartmart.model.Admin;
import smartmart.model.Cashier;
import smartmart.model.Employee;
import smartmart.model.Manager;
import smartmart.model.Role;
import smartmart.model.User;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT e.*, u.username, u.password, u.role, u.full_name AS user_fullname, u.is_active " +
                       "FROM employees e " +
                       "JOIN users u ON e.user_id = u.user_id";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapEmployee(rs));
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

    public Employee getEmployeeById(int employeeId) throws SQLException {
        String query = "SELECT e.*, u.username, u.password, u.role, u.full_name AS user_fullname, u.is_active " +
                       "FROM employees e " +
                       "JOIN users u ON e.user_id = u.user_id " +
                       "WHERE e.employee_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, employeeId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapEmployee(rs);
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

    public boolean addEmployee(Employee employee) throws SQLException {
        String query = "INSERT INTO employees (user_id, full_name, phone, salary, hire_date) VALUES (?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, employee.getUser() != null ? employee.getUser().getUserId() : 0);
            ps.setString(2, employee.getUser() != null ? employee.getUser().getFullName() : "");
            ps.setString(3, employee.getPhone());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, employee.getHireDate());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean updateEmployee(Employee employee) throws SQLException {
        String query = "UPDATE employees SET user_id = ?, full_name = ?, phone = ?, salary = ?, hire_date = ? WHERE employee_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, employee.getUser() != null ? employee.getUser().getUserId() : 0);
            ps.setString(2, employee.getUser() != null ? employee.getUser().getFullName() : "");
            ps.setString(3, employee.getPhone());
            ps.setDouble(4, employee.getSalary());
            ps.setString(5, employee.getHireDate());
            ps.setInt(6, employee.getEmployeeId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean deleteEmployee(int employeeId) throws SQLException {
        String query = "DELETE FROM employees WHERE employee_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, employeeId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        String userFullname = rs.getString("user_fullname");
        boolean isActive = rs.getInt("is_active") == 1;

        Role role = Role.valueOf(roleStr);
        User user = null;
        switch (role) {
            case ADMIN:
                user = new Admin(userId, username, password, role, userFullname, isActive);
                break;
            case MANAGER:
                user = new Manager(userId, username, password, role, userFullname, isActive);
                break;
            case CASHIER:
                user = new Cashier(userId, username, password, role, userFullname, isActive);
                break;
        }

        return new Employee(
                rs.getInt("employee_id"),
                user,
                rs.getString("phone"),
                rs.getDouble("salary"),
                rs.getString("hire_date")
        );
    }
}
