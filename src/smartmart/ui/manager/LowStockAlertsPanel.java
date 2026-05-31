package smartmart.ui.manager;

import smartmart.dao.AlertDAO;
import smartmart.dao.RestockOrderDAO;
import smartmart.model.Alert;
import smartmart.model.Product;
import smartmart.model.RestockOrder;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LowStockAlertsPanel extends JPanel {
    private final AlertDAO alertDAO;
    private final RestockOrderDAO restockOrderDAO;

    private JTable alertsTable;
    private DefaultTableModel alertsModel;
    private JLabel lblUnresolvedCount;

    private List<Alert> currentAlerts;

    public LowStockAlertsPanel() {
        this.alertDAO = new AlertDAO();
        this.restockOrderDAO = new RestockOrderDAO();
        initUI();
        
        // Auto-call check and load
        refreshAlerts();

        // Auto-refresh timer every 60 seconds
        Timer timer = new Timer(60000, e -> refreshAlerts());
        timer.start();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(UIConstants.BACKGROUND_COLOR);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        toolbar.add(UIHelper.createTitleLabel("Low Stock Alerts"));

        JButton btnRefresh = UIHelper.createPrimaryButton("Refresh Alerts");
        btnRefresh.addActionListener(e -> refreshAlerts());
        toolbar.add(btnRefresh);

        JButton btnResolveSelected = UIHelper.createSecondaryButton("Resolve Selected");
        btnResolveSelected.addActionListener(e -> resolveSelected());
        toolbar.add(btnResolveSelected);

        JButton btnResolveAll = UIHelper.createDangerButton("Resolve All");
        btnResolveAll.addActionListener(e -> resolveAll());
        toolbar.add(btnResolveAll);

        lblUnresolvedCount = new JLabel("0 unresolved alerts");
        lblUnresolvedCount.setFont(UIConstants.FONT_BUTTON);
        lblUnresolvedCount.setForeground(UIConstants.WARNING_COLOR);
        toolbar.add(lblUnresolvedCount);

        add(toolbar, BorderLayout.NORTH);

        // Center - Table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(Color.WHITE);

        String[] cols = {"ID", "Product", "Current Stock", "Low Stock Limit", "Shortage", "Message", "Date", "Status"};
        alertsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        alertsTable = UIHelper.createStyledTable();
        alertsTable.setModel(alertsModel);

        alertsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int modelRow = table.convertRowIndexToModel(row);
                if (currentAlerts != null && modelRow < currentAlerts.size()) {
                    Alert alert = currentAlerts.get(modelRow);
                    if (!isSelected) {
                        if (alert.isResolved()) {
                            c.setBackground(UIConstants.SUCCESS_COLOR); // pale green
                        } else {
                            c.setBackground(new Color(255, 235, 204)); // warning color tint
                        }
                    }
                }
                
                if (column == 7) { // Status column
                    if ("🔴 Unresolved".equals(value)) {
                        c.setForeground(UIConstants.DANGER_COLOR);
                    } else {
                        c.setForeground(new Color(0, 153, 0)); // dark green
                    }
                } else {
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }

                return c;
            }
        });

        centerPanel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom - Action Bar
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomBar.setBackground(UIConstants.BACKGROUND_COLOR);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));

        JButton btnCreateRestock = UIHelper.createPrimaryButton("Create Restock Order for Selected");
        btnCreateRestock.addActionListener(e -> createRestockOrder());
        bottomBar.add(btnCreateRestock);

        add(bottomBar, BorderLayout.SOUTH);
    }

    private void refreshAlerts() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                alertDAO.checkAndCreateLowStockAlerts();
                currentAlerts = alertDAO.getAllAlerts();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    alertsModel.setRowCount(0);
                    int unresolvedCount = 0;

                    for (Alert a : currentAlerts) {
                        Product p = a.getProduct();
                        int stock = p != null ? p.getStockQty() : 0;
                        int limit = p != null ? p.getLowStockLimit() : 0;
                        int shortage = Math.max(0, limit - stock);
                        
                        String status = a.isResolved() ? "✅ Resolved" : "🔴 Unresolved";
                        if (!a.isResolved()) unresolvedCount++;

                        alertsModel.addRow(new Object[]{
                                a.getAlertId(),
                                p != null ? p.getProductName() : "Unknown",
                                stock,
                                limit,
                                shortage,
                                a.getMessage(),
                                a.getCreatedAt(),
                                status
                        });
                    }

                    lblUnresolvedCount.setText(unresolvedCount + " unresolved alerts");
                    if (unresolvedCount > 0) {
                        lblUnresolvedCount.setForeground(UIConstants.WARNING_COLOR);
                    } else {
                        lblUnresolvedCount.setForeground(UIConstants.SECONDARY_COLOR);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    UIHelper.showError(LowStockAlertsPanel.this, "Failed to load alerts.");
                }
            }
        };
        worker.execute();
    }

    private void resolveSelected() {
        int selectedRow = alertsTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select an alert to resolve.");
            return;
        }

        int modelRow = alertsTable.convertRowIndexToModel(selectedRow);
        Alert alert = currentAlerts.get(modelRow);

        if (alert.isResolved()) {
            UIHelper.showWarning(this, "This alert is already resolved.");
            return;
        }

        try {
            boolean success = alertDAO.resolveAlert(alert.getAlertId());
            if (success) {
                refreshAlerts();
            } else {
                UIHelper.showError(this, "Failed to resolve alert.");
            }
        } catch (Exception e) {
            UIHelper.showError(this, "Error resolving alert: " + e.getMessage());
        }
    }

    private void resolveAll() {
        int count = 0;
        for (Alert a : currentAlerts) {
            if (!a.isResolved()) count++;
        }

        if (count == 0) {
            UIHelper.showWarning(this, "No unresolved alerts.");
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to resolve all " + count + " alerts?", 
                "Confirm Resolve All", JOptionPane.YES_NO_OPTION);
                
        if (opt == JOptionPane.YES_OPTION) {
            try {
                for (Alert a : currentAlerts) {
                    if (!a.isResolved()) {
                        alertDAO.resolveAlert(a.getAlertId());
                    }
                }
                refreshAlerts();
                UIHelper.showSuccess(this, "All alerts resolved.");
            } catch (Exception e) {
                UIHelper.showError(this, "Error resolving alerts: " + e.getMessage());
            }
        }
    }

    private void createRestockOrder() {
        int selectedRow = alertsTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select an alert to create a restock order.");
            return;
        }

        int modelRow = alertsTable.convertRowIndexToModel(selectedRow);
        Alert alert = currentAlerts.get(modelRow);
        Product product = alert.getProduct();

        if (product == null) {
            UIHelper.showError(this, "Product information is missing.");
            return;
        }

        int shortage = Math.max(0, product.getLowStockLimit() - product.getStockQty());
        int defaultQty = shortage > 0 ? shortage + 10 : 20;

        String input = JOptionPane.showInputDialog(this, 
                "Enter restock quantity for " + product.getProductName() + ":", 
                defaultQty);
        
        if (input == null || input.trim().isEmpty()) {
            return; // cancelled
        }

        try {
            int qty = Integer.parseInt(input.trim());
            if (qty <= 0) {
                UIHelper.showError(this, "Quantity must be greater than zero.");
                return;
            }

            RestockOrder order = new RestockOrder();
            order.setProduct(product);
            order.setSupplier(product.getSupplier());
            order.setQuantity(qty);
            order.setStatus("PENDING");

            boolean success = restockOrderDAO.createRestockOrder(order);
            if (success) {
                UIHelper.showSuccess(this, "Restock order created successfully.");
                // Optionally resolve the alert
                if (!alert.isResolved()) {
                    alertDAO.resolveAlert(alert.getAlertId());
                    refreshAlerts();
                }
            } else {
                UIHelper.showError(this, "Failed to create restock order.");
            }
        } catch (NumberFormatException e) {
            UIHelper.showError(this, "Invalid quantity entered.");
        } catch (Exception e) {
            UIHelper.showError(this, "Error creating restock order: " + e.getMessage());
        }
    }
}
