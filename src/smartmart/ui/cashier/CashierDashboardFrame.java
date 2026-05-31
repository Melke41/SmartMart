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
    }

    @Override
    protected void initModules() {
        String[] modules = {
            "New Sale", "Transaction History"
        };

        for (String mod : modules) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(Color.WHITE);
            
            JLabel lblPlaceholder = new JLabel(mod + " — Coming in Phase 7");
            lblPlaceholder.setFont(UIConstants.FONT_TITLE);
            lblPlaceholder.setForeground(UIConstants.TEXT_SECONDARY);
            
            panel.add(lblPlaceholder);
            contentPanel.add(panel, mod);
        }

        // Display New Sale module by default
        showModule("New Sale");
    }
}
