package smartmart.ui.admin;

import smartmart.exception.DuplicateUsernameException;
import smartmart.model.*;
import smartmart.service.EmployeeService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddEditEmployeeDialog extends JDialog {
    private final JTextField txtFullName;
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JComboBox<Role> comboRole;
    private final JTextField txtPhone;
    private final JTextField txtSalary;
    private final JTextField txtHireDate;

    private final EmployeeService employeeService;
    private final EmployeeManagementPanel parentPanel;
    private final Employee employeeToEdit;

    public AddEditEmployeeDialog(Frame owner, EmployeeManagementPanel parentPanel, Employee employeeToEdit) {
        super(owner, employeeToEdit == null ? "Add Employee" : "Edit Employee", true);
        this.parentPanel = parentPanel;
        this.employeeToEdit = employeeToEdit;
        this.employeeService = new EmployeeService();

        setSize(420, employeeToEdit == null ? 480 : 380);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel lblFullName = new JLabel("Full Name:");
        lblFullName.setFont(UIConstants.FONT_BUTTON);
        txtFullName = UIHelper.createStyledTextField();

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(UIConstants.FONT_BUTTON);
        txtUsername = UIHelper.createStyledTextField();

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(UIConstants.FONT_BUTTON);
        txtPassword = UIHelper.createStyledPasswordField();

        JLabel lblRole = new JLabel("System Role:");
        lblRole.setFont(UIConstants.FONT_BUTTON);
        comboRole = new JComboBox<>(Role.values());
        comboRole.setFont(UIConstants.FONT_BODY);
        comboRole.setBackground(Color.WHITE);

        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(UIConstants.FONT_BUTTON);
        txtPhone = UIHelper.createStyledTextField();

        JLabel lblSalary = new JLabel("Salary (ETB):");
        lblSalary.setFont(UIConstants.FONT_BUTTON);
        txtSalary = UIHelper.createStyledTextField();

        JLabel lblHireDate = new JLabel("Hire Date (yyyy-MM-dd):");
        lblHireDate.setFont(UIConstants.FONT_BUTTON);
        txtHireDate = UIHelper.createStyledTextField();

        // Standard Layout Rendering
        int row = 0;
        addComponent(mainPanel, lblFullName, gbc, 0, row, 1);
        addComponent(mainPanel, txtFullName, gbc, 1, row++, 2);

        addComponent(mainPanel, lblUsername, gbc, 0, row, 1);
        addComponent(mainPanel, txtUsername, gbc, 1, row++, 2);

        if (employeeToEdit == null) {
            // Password only shown in ADD mode
            addComponent(mainPanel, lblPassword, gbc, 0, row, 1);
            addComponent(mainPanel, txtPassword, gbc, 1, row++, 2);
            
            // Set default hire date to today
            txtHireDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } else {
            // Username not editable in EDIT mode
            txtUsername.setText(employeeToEdit.getUser().getUsername());
            txtUsername.setEnabled(false);
        }

        addComponent(mainPanel, lblRole, gbc, 0, row, 1);
        addComponent(mainPanel, comboRole, gbc, 1, row++, 2);

        addComponent(mainPanel, lblPhone, gbc, 0, row, 1);
        addComponent(mainPanel, txtPhone, gbc, 1, row++, 2);

        addComponent(mainPanel, lblSalary, gbc, 0, row, 1);
        addComponent(mainPanel, txtSalary, gbc, 1, row++, 2);

        addComponent(mainPanel, lblHireDate, gbc, 0, row, 1);
        addComponent(mainPanel, txtHireDate, gbc, 1, row++, 2);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(new Color(245, 245, 250));
        buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));

        JButton btnSave = UIHelper.createPrimaryButton("Save");
        btnSave.setPreferredSize(new Dimension(80, UIConstants.BUTTON_HEIGHT));

        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(80, UIConstants.BUTTON_HEIGHT));

        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnSave);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Prepopulate on Edit
        if (employeeToEdit != null) {
            txtFullName.setText(employeeToEdit.getUser().getFullName());
            txtPhone.setText(employeeToEdit.getPhone());
            txtSalary.setText(String.valueOf(employeeToEdit.getSalary()));
            txtHireDate.setText(employeeToEdit.getHireDate());
            comboRole.setSelectedItem(employeeToEdit.getUser().getRole());
        }

        // Action Listeners
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int gridx, int gridy, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        panel.add(comp, gbc);
    }

    private void handleSave() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = (txtPassword != null) ? new String(txtPassword.getPassword()) : "";
        Role role = (Role) comboRole.getSelectedItem();
        String phone = txtPhone.getText().trim();
        String salaryStr = txtSalary.getText().trim();
        String hireDate = txtHireDate.getText().trim();

        // Validation
        if (fullName.isEmpty() || username.isEmpty() || phone.isEmpty() || salaryStr.isEmpty() || hireDate.isEmpty()) {
            UIHelper.showWarning(this, "All fields are required.");
            return;
        }

        if (employeeToEdit == null && password.isEmpty()) {
            UIHelper.showWarning(this, "Password is required for a new employee.");
            return;
        }

        double salary;
        try {
            salary = Double.parseDouble(salaryStr);
            if (salary < 0) {
                UIHelper.showWarning(this, "Salary cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            UIHelper.showWarning(this, "Please enter a valid numeric value for salary.");
            return;
        }

        // Validate date format yyyy-MM-dd
        if (!hireDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            UIHelper.showWarning(this, "Hire date must match the format yyyy-MM-dd.");
            return;
        }

        try {
            if (employeeToEdit == null) {
                // Create User based on role
                User newUser;
                switch (role) {
                    case ADMIN:
                        newUser = new Admin(0, username, "", role, fullName, true);
                        break;
                    case MANAGER:
                        newUser = new Manager(0, username, "", role, fullName, true);
                        break;
                    case CASHIER:
                        newUser = new Cashier(0, username, "", role, fullName, true);
                        break;
                    default:
                        UIHelper.showError(this, "Unsupported role value selected.");
                        return;
                }

                Employee newEmp = new Employee(0, newUser, phone, salary, hireDate);
                employeeService.addEmployee(newEmp, password);
                UIHelper.showSuccess(this, "Employee profile added successfully.");
            } else {
                // EDIT mode
                User user = employeeToEdit.getUser();
                user.setFullName(fullName);
                user.setRole(role);
                employeeToEdit.setPhone(phone);
                employeeToEdit.setSalary(salary);
                employeeToEdit.setHireDate(hireDate);

                employeeService.updateEmployee(employeeToEdit);
                UIHelper.showSuccess(this, "Employee profile updated successfully.");
            }
            parentPanel.refreshTable();
            dispose();
        } catch (DuplicateUsernameException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to save employee: " + ex.getMessage());
        }
    }
}
