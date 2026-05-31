package smartmart.ui.admin;

import smartmart.dao.AlertDAO;
import smartmart.model.Alert;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AlertsPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final AlertDAO alertDAO;

    public AlertsPanel() {
        this.alertDAO = new AlertDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Low Stock Alerts");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        JButton btnRefresh = UIHelper.createSecondaryButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(90, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnResolve = UIHelper.createPrimaryButton("Resolve Selected");
        btnResolve.setPreferredSize(new Dimension(160, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnRefresh);
        toolbarPanel.add(btnResolve);
        add(toolbarPanel, BorderLayout.NORTH);

        // Center Table Setup
        String[] columns = {"ID", "Product", "Message", "Status", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    int modelRow = convertRowIndexToModel(row);
                    String status = (String) getModel().getValueAt(modelRow, 3);
                    if (status != null && status.contains("UNRESOLVED")) {
                        c.setBackground(UIConstants.WARNING_COLOR);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    }
                }
                return c;
            }
        };

        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_BODY);
        table.setGridColor(new Color(220, 220, 225));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        table.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(UIConstants.FONT_BUTTON);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 35));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Event Handling
        
        // Refresh Alerts
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAndRefresh();
            }
        });

        // Resolve Alert
        btnResolve.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(AlertsPanel.this, "Please select an alert row to resolve.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int alertId = (Integer) tableModel.getValueAt(modelIndex, 0);

                int confirm = JOptionPane.showConfirmDialog(
                        AlertsPanel.this,
                        "Are you sure you want to resolve alert #" + alertId + "?",
                        "Confirm Resolution",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        alertDAO.resolveAlert(alertId);
                        UIHelper.showSuccess(AlertsPanel.this, "Alert resolved successfully.");
                        refreshTable();
                    } catch (Exception ex) {
                        UIHelper.showError(AlertsPanel.this, "Failed to resolve alert: " + ex.getMessage());
                    }
                }
            }
        });

        // Load initially
        checkAndRefresh();
    }

    private void checkAndRefresh() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Auto-call check and create alerts to catch any low stock changes
                alertDAO.checkAndCreateLowStockAlerts();
                return null;
            }

            @Override
            protected void done() {
                refreshTable();
            }
        };
        worker.execute();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        SwingWorker<List<Alert>, Void> worker = new SwingWorker<List<Alert>, Void>() {
            @Override
            protected List<Alert> doInBackground() throws Exception {
                return alertDAO.getUnresolvedAlerts();
            }

            @Override
            protected void done() {
                try {
                    List<Alert> alerts = get();
                    for (Alert a : alerts) {
                        String statusStr = "<html><font color='#994400'><b>UNRESOLVED</b></font></html>";
                        tableModel.addRow(new Object[]{
                                a.getAlertId(),
                                a.getProduct() != null ? a.getProduct().getProductName() : "Unknown",
                                a.getMessage(),
                                statusStr,
                                a.getCreatedAt()
                        });
                    }
                } catch (Exception ex) {
                    UIHelper.showError(AlertsPanel.this, "Failed to load low stock alerts: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
