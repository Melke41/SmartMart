package smartmart.ui.admin;

import smartmart.model.Supplier;
import smartmart.service.SupplierService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierManagementPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch;
    private final SupplierService supplierService;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private List<Supplier> supplierList;

    public SupplierManagementPanel() {
        this.supplierService = new SupplierService();
        this.supplierList = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Supplier Management");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        txtSearch = UIHelper.createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, UIConstants.FORM_FIELD_HEIGHT));
        txtSearch.setText("Search suppliers...");
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search suppliers...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search suppliers...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = UIHelper.createPrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(80, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnAdd = UIHelper.createSuccessButton("Add Supplier");
        btnAdd.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnEdit = UIHelper.createPrimaryButton("Edit Supplier");
        btnEdit.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnDelete = UIHelper.createDangerButton("Delete Supplier");
        btnDelete.setPreferredSize(new Dimension(140, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(btnSearch);
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        add(toolbarPanel, BorderLayout.NORTH);

        // Center Table Setup
        String[] columns = {"ID", "Name", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = UIHelper.createStyledTable();
        table.setModel(tableModel);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Event Handling
        
        // Search filter live typing
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = txtSearch.getText().trim();
                if (query.equals("Search suppliers...") || query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = txtSearch.getText().trim();
                if (query.equals("Search suppliers...") || query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        // Add Button Dialog Trigger
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEditSupplierDialog dialog = new AddEditSupplierDialog((Frame) SwingUtilities.getWindowAncestor(SupplierManagementPanel.this), SupplierManagementPanel.this, null);
                dialog.setVisible(true);
            }
        });

        // Edit Button Dialog Trigger
        btnEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openEditDialog();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    openEditDialog();
                }
            }
        });

        // Delete Button action
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(SupplierManagementPanel.this, "Please select a supplier row to delete.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int supplierId = (Integer) tableModel.getValueAt(modelIndex, 0);
                String supplierName = (String) tableModel.getValueAt(modelIndex, 1);

                int confirm = JOptionPane.showConfirmDialog(
                        SupplierManagementPanel.this,
                        "Are you sure you want to delete supplier '" + supplierName + "'?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        supplierService.deleteSupplier(supplierId);
                        UIHelper.showSuccess(SupplierManagementPanel.this, "Supplier deleted successfully.");
                        refreshTable();
                    } catch (Exception ex) {
                        UIHelper.showError(SupplierManagementPanel.this, "Failed to delete supplier: " + ex.getMessage() + "\n(Verify no products are linked to this supplier)");
                    }
                }
            }
        });

        // Initial load
        refreshTable();
    }

    private void openEditDialog() {
        int selectedRowIndex = table.getSelectedRow();
        if (selectedRowIndex == -1) {
            UIHelper.showWarning(this, "Please select a supplier row to edit.");
            return;
        }

        int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
        int supplierId = (Integer) tableModel.getValueAt(modelIndex, 0);

        Supplier selectedSup = null;
        for (Supplier s : supplierList) {
            if (s.getSupplierId() == supplierId) {
                selectedSup = s;
                break;
            }
        }

        if (selectedSup != null) {
            AddEditSupplierDialog dialog = new AddEditSupplierDialog((Frame) SwingUtilities.getWindowAncestor(this), this, selectedSup);
            dialog.setVisible(true);
        }
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<List<Supplier>, Void>() {
            @Override
            protected List<Supplier> doInBackground() throws Exception {
                return supplierService.getAllSuppliers();
            }

            @Override
            protected void done() {
                try {
                    supplierList = get();
                    for (Supplier s : supplierList) {
                        tableModel.addRow(new Object[]{
                                s.getSupplierId(),
                                s.getName(),
                                s.getContactPhone(),
                                s.getEmail(),
                                s.getAddress()
                        });
                    }
                } catch (Exception ex) {
                    UIHelper.showError(SupplierManagementPanel.this, "Error loading suppliers: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
