package smartmart.ui.manager;

import smartmart.model.Manager;
import smartmart.ui.BaseDashboardFrame;
import smartmart.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerDashboardFrame extends BaseDashboardFrame {

    public ManagerDashboardFrame(Manager manager) {
        super(manager);
    }

    @Override
    protected void buildSidebar() {
        String[] menuItems = {
            "Dashboard", "Sales Report", "Inventory Report", "EOD Report", "Low Stock Alerts"
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

        sidebarPanel.add(Box.createVerticalGlue());
    }

    @Override
    protected void initModules() {
        String[] modules = {
            "Dashboard", "Sales Report", "Inventory Report", "EOD Report", "Low Stock Alerts"
        };

        for (String mod : modules) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            
            JLabel lblPlaceholder = new JLabel(mod + " — Coming in Phase 8");
            lblPlaceholder.setFont(UIConstants.FONT_TITLE);
            lblPlaceholder.setForeground(UIConstants.TEXT_SECONDARY);
            
            panel.add(lblPlaceholder);
            contentPanel.add(panel, mod);
        }

        // Display Dashboard module by default
        showModule("Dashboard");
    }
}
