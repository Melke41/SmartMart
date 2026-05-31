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

        // Add manager name label
        JLabel lblManager = new JLabel("Manager: " + currentUser.getFullName());
        lblManager.setFont(UIConstants.FONT_SMALL);
        lblManager.setForeground(Color.WHITE);
        lblManager.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        lblManager.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(lblManager);
    }

    @Override
    protected void initModules() {
        contentPanel.add(new ManagerOverviewPanel(), "Dashboard");
        contentPanel.add(new SalesReportPanel(), "Sales Report");
        contentPanel.add(new InventoryReportPanel(), "Inventory Report");
        contentPanel.add(new EODReportPanel(), "EOD Report");
        contentPanel.add(new LowStockAlertsPanel(), "Low Stock Alerts");

        // Display Dashboard module by default
        showModule("Dashboard");
    }
}
