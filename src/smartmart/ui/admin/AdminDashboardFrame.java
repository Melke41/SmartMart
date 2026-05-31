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
        String[] modules = {
            "Dashboard", "Products", "Categories", "Suppliers", 
            "Employees", "Users", "Restock Orders", "Alerts"
        };

        for (String mod : modules) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            
            JLabel lblPlaceholder = new JLabel(mod + " — Coming in Phase 6");
            lblPlaceholder.setFont(UIConstants.FONT_TITLE);
            lblPlaceholder.setForeground(UIConstants.TEXT_SECONDARY);
            
            panel.add(lblPlaceholder);
            contentPanel.add(panel, mod);
        }

        // Display Dashboard module by default
        showModule("Dashboard");
    }
}
