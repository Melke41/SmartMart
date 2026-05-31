package smartmart.ui.admin;

import smartmart.model.Employee;
import smartmart.service.EmployeeService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManagementPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final EmployeeService employeeService;
    private List<Employee> employeeList;

    public EmployeeManagementPanel() {
        this.employeeService = new EmployeeService();
        this.employeeList = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Employee Management");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        JButton btnAdd = UIHelper.createSuccessButton("Add Employee");
        btnAdd.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnEdit = UIHelper.createPrimaryButton("Edit Employee");
        btnEdit.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnDelete = UIHelper.createDangerButton("Delete Employee");
        btnDelete.setPreferredSize(new Dimension(140, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        add(toolbarPanel, BorderLayout.NORTH);

        // Center Table Setup
        String[] columns = {"ID", "Full Name", "Role", "Phone", "Salary (ETB)", "Hire Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createStyledTable();
        table.setModel(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Event Handling
        
        // Add button click
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEditEmployeeDialog dialog = new AddEditEmployeeDialog((Frame) SwingUtilities.getWindowAncestor(EmployeeManagementPanel.this), EmployeeManagementPanel.this, null);
                dialog.setVisible(true);
            }
        });

        // Edit button click
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditDialog();
            }
        });

        // Double click trigger
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openEditDialog();
                }
            }
        });

        // Delete button click
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(EmployeeManagementPanel.this, "Please select an employee row to delete.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int employeeId = (Integer) tableModel.getValueAt(modelIndex, 0);
                String fullName = (String) tableModel.getValueAt(modelIndex, 1);

                int confirm = JOptionPane.showConfirmDialog(
                        EmployeeManagementPanel.this,
                        "Are you sure you want to delete employee '" + fullName + "'?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        employeeService.deleteEmployee(employeeId);
                        UIHelper.showSuccess(EmployeeManagementPanel.this, "Employee deleted successfully.");
                        refreshTable();
                    } catch (Exception ex) {
                        UIHelper.showError(EmployeeManagementPanel.this, "Failed to delete employee: " + ex.getMessage());
                    }
                }
            }
        });

        // Initial Load
        refreshTable();
    }

    private void openEditDialog() {
        int selectedRowIndex = table.getSelectedRow();
        if (selectedRowIndex == -1) {
            UIHelper.showWarning(this, "Please select an employee row to edit.");
            return;
        }

        int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
        int employeeId = (Integer) tableModel.getValueAt(modelIndex, 0);

        Employee selectedEmp = null;
        for (Employee emp : employeeList) {
            if (emp.getEmployeeId() == employeeId) {
                selectedEmp = emp;
                break;
            }
        }

        if (selectedEmp != null) {
            AddEditEmployeeDialog dialog = new AddEditEmployeeDialog((Frame) SwingUtilities.getWindowAncestor(this), this, selectedEmp);
            dialog.setVisible(true);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        SwingWorker<List<Employee>, Void> worker = new SwingWorker<List<Employee>, Void>() {
            @Override
            protected List<Employee> doInBackground() throws Exception {
                return employeeService.getAllEmployees();
            }

            @Override
            protected void done() {
                try {
                    employeeList = get();
                    for (Employee emp : employeeList) {
                        tableModel.addRow(new Object[]{
                                emp.getEmployeeId(),
                                emp.getUser() != null ? emp.getUser().getFullName() : "Unknown",
                                emp.getUser() != null && emp.getUser().getRole() != null ? emp.getUser().getRole().getDisplayName() : "Unknown",
                                emp.getPhone(),
                                emp.getSalary(),
                                emp.getHireDate()
                        });
                    }
                } catch (Exception ex) {
                    UIHelper.showError(EmployeeManagementPanel.this, "Error loading employees: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
