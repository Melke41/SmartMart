package smartmart.ui.cashier;

import smartmart.model.Cashier;
import smartmart.ui.BaseDashboardFrame;
import smartmart.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CashierDashboardFrame extends BaseDashboardFrame {

    public CashierDashboardFrame(Cashier cashier) {
        super(cashier);
    }

    @Override
    protected void buildSidebar() {
        String[] menuItems = {
            "New Sale", "Transaction History"
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

        // Add a small stats section at the bottom of sidebar (optional extra details if needed, per step 4)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(UIConstants.PRIMARY_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel statsLabel = new JLabel("Today's Sales:");
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setFont(UIConstants.FONT_BODY.deriveFont(Font.BOLD));
        statsPanel.add(statsLabel);

        // A placeholder for the actual counts that get updated by events in a real complex app
        // Currently, it's just visual structure.
        JLabel countLabel = new JLabel("Check history tab");
        countLabel.setForeground(new Color(200, 200, 255));
        countLabel.setFont(UIConstants.FONT_SMALL);
        statsPanel.add(countLabel);

        sidebarPanel.add(statsPanel);
    }

    @Override
    protected void initModules() {
        // Initialize the actual panels instead of placeholders
        contentPanel.add(new POSPanel(), "New Sale");
        contentPanel.add(new TransactionHistoryPanel(), "Transaction History");

        // Display New Sale module by default
        showModule("New Sale");
    }
}
