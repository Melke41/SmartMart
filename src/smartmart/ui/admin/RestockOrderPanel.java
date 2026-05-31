package smartmart.ui.admin;

import smartmart.dao.RestockOrderDAO;
import smartmart.model.RestockOrder;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RestockOrderPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final RestockOrderDAO restockOrderDAO;

    public RestockOrderPanel() {
        this.restockOrderDAO = new RestockOrderDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Restock Orders");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        JButton btnCreate = UIHelper.createSuccessButton("Create Order");
        btnCreate.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnReceive = UIHelper.createPrimaryButton("Mark Received");
        btnReceive.setPreferredSize(new Dimension(140, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnCancel = UIHelper.createDangerButton("Cancel Order");
        btnCancel.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnCreate);
        toolbarPanel.add(btnReceive);
        toolbarPanel.add(btnCancel);
        add(toolbarPanel, BorderLayout.NORTH);

        // Center Table Setup
        String[] columns = {"ID", "Product", "Supplier", "Quantity", "Status", "Order Date", "Received Date"};
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
        
        // Create Order click
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateRestockOrderDialog dialog = new CreateRestockOrderDialog((Frame) SwingUtilities.getWindowAncestor(RestockOrderPanel.this), RestockOrderPanel.this);
                dialog.setVisible(true);
            }
        });

        // Mark Received click
        btnReceive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(RestockOrderPanel.this, "Please select an order to mark as received.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int orderId = (Integer) tableModel.getValueAt(modelIndex, 0);
                String productName = (String) tableModel.getValueAt(modelIndex, 1);
                String statusText = (String) tableModel.getValueAt(modelIndex, 4);

                if (statusText.contains("RECEIVED")) {
                    UIHelper.showWarning(RestockOrderPanel.this, "This order is already marked as RECEIVED.");
                    return;
                }
                if (statusText.contains("CANCELLED")) {
                    UIHelper.showWarning(RestockOrderPanel.this, "Cannot receive a CANCELLED order.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        RestockOrderPanel.this,
                        "Are you sure you want to mark restock order #" + orderId + " (" + productName + ") as RECEIVED?\nThis will increase product inventory stock.",
                        "Confirm Receipt",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        restockOrderDAO.markAsReceived(orderId);
                        UIHelper.showSuccess(RestockOrderPanel.this, "Order received successfully. Stock inventory updated.");
                        refreshTable();
                    } catch (Exception ex) {
                        UIHelper.showError(RestockOrderPanel.this, "Failed to mark order as received: " + ex.getMessage());
                    }
                }
            }
        });

        // Cancel Order click
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(RestockOrderPanel.this, "Please select an order to cancel.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int orderId = (Integer) tableModel.getValueAt(modelIndex, 0);
                String statusText = (String) tableModel.getValueAt(modelIndex, 4);

                if (statusText.contains("RECEIVED")) {
                    UIHelper.showWarning(RestockOrderPanel.this, "Cannot cancel an order that has already been RECEIVED.");
                    return;
                }
                if (statusText.contains("CANCELLED")) {
                    UIHelper.showWarning(RestockOrderPanel.this, "This order is already CANCELLED.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        RestockOrderPanel.this,
                        "Are you sure you want to cancel order #" + orderId + "?",
                        "Confirm Cancellation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        restockOrderDAO.cancelOrder(orderId);
                        UIHelper.showSuccess(RestockOrderPanel.this, "Restock order cancelled successfully.");
                        refreshTable();
                    } catch (Exception ex) {
                        UIHelper.showError(RestockOrderPanel.this, "Failed to cancel restock order: " + ex.getMessage());
                    }
                }
            }
        });

        // Load initially
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        SwingWorker<List<RestockOrder>, Void> worker = new SwingWorker<List<RestockOrder>, Void>() {
            @Override
            protected List<RestockOrder> doInBackground() throws Exception {
                return restockOrderDAO.getAllOrders();
            }

            @Override
            protected void done() {
                try {
                    List<RestockOrder> orders = get();
                    for (RestockOrder order : orders) {
                        String statusStr = order.getStatus();
                        String htmlStatus = "";
                        if ("PENDING".equalsIgnoreCase(statusStr)) {
                            htmlStatus = "<html><font color='#ff9900'><b>🕐 PENDING</b></font></html>";
                        } else if ("RECEIVED".equalsIgnoreCase(statusStr)) {
                            htmlStatus = "<html><font color='#009933'><b>✅ RECEIVED</b></font></html>";
                        } else if ("CANCELLED".equalsIgnoreCase(statusStr)) {
                            htmlStatus = "<html><font color='#cc0000'><b>❌ CANCELLED</b></font></html>";
                        } else {
                            htmlStatus = statusStr;
                        }

                        tableModel.addRow(new Object[]{
                                order.getOrderId(),
                                order.getProduct() != null ? order.getProduct().getProductName() : "Unknown",
                                order.getSupplier() != null ? order.getSupplier().getName() : "Unknown",
                                order.getQuantity(),
                                htmlStatus,
                                order.getOrderDate(),
                                order.getReceivedDate() != null ? order.getReceivedDate() : "N/A"
                        });
                    }
                } catch (Exception ex) {
                    UIHelper.showError(RestockOrderPanel.this, "Error loading restock orders: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
