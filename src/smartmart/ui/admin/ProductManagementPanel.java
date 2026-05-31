package smartmart.ui.admin;

import smartmart.exception.ProductHasSalesHistoryException;
import smartmart.exception.SmartMartException;
import smartmart.model.Product;
import smartmart.service.ProductService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManagementPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField txtSearch;
    private final JLabel lblStatus;
    private final ProductService productService;
    private final TableRowSorter<DefaultTableModel> rowSorter;
    private List<Product> productList;

    public ProductManagementPanel() {
        this.productService = new ProductService();
        this.productList = new ArrayList<>();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Product Management");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        txtSearch = UIHelper.createStyledTextField();
        txtSearch.setPreferredSize(new Dimension(200, UIConstants.FORM_FIELD_HEIGHT));
        txtSearch.setText("Search products...");
        txtSearch.setForeground(Color.GRAY);

        // Search placeholder behavior
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().equals("Search products...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(UIConstants.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search products...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = UIHelper.createPrimaryButton("Search");
        btnSearch.setPreferredSize(new Dimension(80, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnAdd = UIHelper.createSuccessButton("Add Product");
        btnAdd.setPreferredSize(new Dimension(120, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnDelete = UIHelper.createDangerButton("Delete Product");
        btnDelete.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        toolbarPanel.add(txtSearch);
        toolbarPanel.add(btnSearch);
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnDelete);

        add(toolbarPanel, BorderLayout.NORTH);

        // Center Table Setup
        String[] columns = {"ID", "Name", "Category", "Supplier", "Price (ETB)", "Stock", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    int modelRow = convertRowIndexToModel(row);
                    String status = (String) getModel().getValueAt(modelRow, 6);
                    if (status != null && status.contains("Low Stock")) {
                        c.setBackground(UIConstants.LOW_STOCK_COLOR);
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    }
                }
                return c;
            }
        };

        // Standard styling
        table.setRowHeight(30);
        table.setFont(UIConstants.FONT_BODY);
        table.setGridColor(new Color(220, 220, 225));
        table.setShowGrid(true);
        table.setFillsViewportHeight(true);

        // Custom Header Customization
        table.getTableHeader().setBackground(UIConstants.PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(UIConstants.FONT_BUTTON);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 35));

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(220, 220, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setOpaque(false);
        statusBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        lblStatus = new JLabel("Total products: 0 | Low stock: 0");
        lblStatus.setFont(UIConstants.FONT_BODY);
        lblStatus.setForeground(UIConstants.TEXT_SECONDARY);
        statusBar.add(lblStatus, BorderLayout.WEST);

        add(statusBar, BorderLayout.SOUTH);

        // Event Handling
        
        // Search Live Filtering
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = txtSearch.getText().trim();
                if (query.equals("Search products...") || query.isEmpty()) {
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
                if (query.equals("Search products...") || query.isEmpty()) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
                }
            }
        });

        // Add Product Dialog Action
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddEditProductDialog dialog = new AddEditProductDialog((Frame) SwingUtilities.getWindowAncestor(ProductManagementPanel.this), ProductManagementPanel.this, null);
                dialog.setVisible(true);
            }
        });

        // Double click row edit trigger
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    int selectedRowIndex = table.getSelectedRow();
                    int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                    int productId = (Integer) tableModel.getValueAt(modelIndex, 0);

                    // Find product by id from local cache list
                    Product selectedProd = null;
                    for (Product p : productList) {
                        if (p.getProductId() == productId) {
                            selectedProd = p;
                            break;
                        }
                    }
                    if (selectedProd != null) {
                        AddEditProductDialog dialog = new AddEditProductDialog((Frame) SwingUtilities.getWindowAncestor(ProductManagementPanel.this), ProductManagementPanel.this, selectedProd);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        // Delete Product Action
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowIndex = table.getSelectedRow();
                if (selectedRowIndex == -1) {
                    UIHelper.showWarning(ProductManagementPanel.this, "Please select a product row to delete.");
                    return;
                }

                int modelIndex = table.convertRowIndexToModel(selectedRowIndex);
                int productId = (Integer) tableModel.getValueAt(modelIndex, 0);
                String productName = (String) tableModel.getValueAt(modelIndex, 1);

                int confirm = JOptionPane.showConfirmDialog(
                        ProductManagementPanel.this,
                        "Are you sure you want to delete '" + productName + "'?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        productService.deleteProduct(productId);
                        UIHelper.showSuccess(ProductManagementPanel.this, "Product deleted successfully.");
                        refreshTable();
                    } catch (ProductHasSalesHistoryException ex) {
                        UIHelper.showWarning(ProductManagementPanel.this, ex.getMessage());
                    } catch (SmartMartException ex) {
                        UIHelper.showError(ProductManagementPanel.this, ex.getMessage());
                    }
                }
            }
        });

        // Load data initially
        refreshTable();
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        SwingWorker<List<Product>, Void> worker = new SwingWorker<List<Product>, Void>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.getAllProducts();
            }

            @Override
            protected void done() {
                try {
                    productList = get();
                    int lowStockCount = 0;

                    for (Product p : productList) {
                        boolean isLow = p.getStockQty() <= p.getLowStockLimit();
                        String statusStr;
                        if (isLow) {
                            lowStockCount++;
                            statusStr = "<html><font color='#cc6600'><b>⚠ Low Stock</b></font></html>";
                        } else {
                            statusStr = "<html><font color='#009933'><b>✅ OK</b></font></html>";
                        }

                        tableModel.addRow(new Object[]{
                                p.getProductId(),
                                p.getProductName(),
                                p.getCategory() != null ? p.getCategory().getCategoryName() : "None",
                                p.getSupplier() != null ? p.getSupplier().getName() : "None",
                                p.getPrice(),
                                p.getStockQty(),
                                statusStr
                        });
                    }

                    lblStatus.setText("Total products: " + productList.size() + " | Low stock: " + lowStockCount);
                } catch (Exception ex) {
                    lblStatus.setText("Error loading products: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
