package smartmart.ui.admin;

import smartmart.model.Admin;
import smartmart.ui.BaseDashboardFrame;
import smartmart.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminDashboardFrame extends BaseDashboardFrame {

    public AdminDashboardFrame(Admin admin) {
        super(admin);
    }

    @Override
    protected void buildSidebar() {
        String[] menuItems = {
            "Dashboard", "Products", "Categories", "Suppliers", 
            "Employees", "Users", "Restock Orders", "Alerts"
        };

        for (String item : menuItems) {
            JButton btn = createSidebarButton(item);
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showModule(item);
                }
            });
            sidebarPanel.add(btn);
        }

        // Add vertical spacing glue to push buttons up
        sidebarPanel.add(Box.createVerticalGlue());
    }

    @Override
    protected void initModules() {
        contentPanel.add(new AdminOverviewPanel(), "Dashboard");
        contentPanel.add(new ProductManagementPanel(), "Products");
        contentPanel.add(new CategoryManagementPanel(), "Categories");
        contentPanel.add(new SupplierManagementPanel(), "Suppliers");
        contentPanel.add(new EmployeeManagementPanel(), "Employees");

        // Keep Users as a placeholder
        JPanel usersPanel = new JPanel(new GridBagLayout());
        usersPanel.setBackground(Color.WHITE);
        JLabel lblUsers = new JLabel("User Management — Admin Only");
        lblUsers.setFont(UIConstants.FONT_TITLE);
        lblUsers.setForeground(UIConstants.TEXT_SECONDARY);
        usersPanel.add(lblUsers);
        contentPanel.add(usersPanel, "Users");

        contentPanel.add(new RestockOrderPanel(), "Restock Orders");
        contentPanel.add(new AlertsPanel(), "Alerts");

        // Display Dashboard module by default
        showModule("Dashboard");
    }
}
