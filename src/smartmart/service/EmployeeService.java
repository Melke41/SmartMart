package smartmart.service;

import smartmart.dao.EmployeeDAO;
import smartmart.dao.UserDAO;
import smartmart.exception.DuplicateUsernameException;
import smartmart.exception.SmartMartException;
import smartmart.model.Employee;
import smartmart.model.Role;
import smartmart.model.User;
import smartmart.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;
    private final UserDAO userDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
        this.userDAO = new UserDAO();
    }

    public List<Employee> getAllEmployees() throws SmartMartException {
        try {
            return employeeDAO.getAllEmployees();
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving all employees: " + e.getMessage(), e);
        }
    }

    public void addEmployee(Employee employee, String plainPassword) throws SmartMartException, DuplicateUsernameException {
        AuthService.requireRole(Role.ADMIN);
        
        if (employee == null || employee.getUser() == null) {
            throw new SmartMartException("Employee and associated user profile cannot be null.");
        }

        try {
            // Check duplicate username
            String username = employee.getUser().getUsername();
            List<User> allUsers = userDAO.getAllUsers();
            for (User u : allUsers) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    throw new DuplicateUsernameException(username);
                }
            }

            // Set user password and add user
            employee.getUser().setPassword(PasswordUtil.hashPassword(plainPassword));
            userDAO.addUser(employee.getUser()); // this sets the generated userId on the user object

            // Save employee linking to the user
            employeeDAO.addEmployee(employee);

        } catch (SQLException e) {
            throw new SmartMartException("Database error adding employee: " + e.getMessage(), e);
        }
    }

    public void updateEmployee(Employee employee) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        
        if (employee == null || employee.getUser() == null) {
            throw new SmartMartException("Employee and associated user profile cannot be null.");
        }

        try {
            userDAO.updateUser(employee.getUser());
            employeeDAO.updateEmployee(employee);
        } catch (SQLException e) {
            throw new SmartMartException("Database error updating employee: " + e.getMessage(), e);
        }
    }

    public void deleteEmployee(int employeeId) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        try {
            employeeDAO.deleteEmployee(employeeId);
        } catch (SQLException e) {
            throw new SmartMartException("Database error deleting employee: " + e.getMessage(), e);
        }
    }
}
