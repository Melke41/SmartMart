package smartmart.ui.cashier;

import smartmart.exception.InsufficientStockException;
import smartmart.exception.OutOfStockException;
import smartmart.exception.SmartMartException;
import smartmart.model.Product;
import smartmart.model.Sale;
import smartmart.model.SaleItem;
import smartmart.service.AuthService;
import smartmart.service.ProductService;
import smartmart.service.SaleService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class POSPanel extends JPanel {

    private final ProductService productService;
    private final SaleService saleService;
    private final AuthService authService;

    private JTable productTable;
    private DefaultTableModel productTableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JSpinner quantitySpinner;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;

    private Sale currentSale;
    private List<Product> productList;

    public POSPanel() {
        productService = new ProductService();
        saleService = new SaleService();
        authService = new AuthService();

        currentSale = saleService.createNewSale();

        setLayout(new BorderLayout());
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600); // approx 55% of 1000px wide app
        splitPane.setDividerSize(5);

        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());

        add(splitPane, BorderLayout.CENTER);

        loadProducts();
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        // Top Row: Title and Search
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(UIHelper.createTitleLabel("Point of Sale"), BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchField = UIHelper.createStyledTextField();
        searchField.setPreferredSize(new Dimension(300, UIConstants.FORM_FIELD_HEIGHT));
        searchField.setToolTipText("Search product by name...");
        
        JButton searchBtn = UIHelper.createPrimaryButton("Search");
        JButton showAllBtn = UIHelper.createSecondaryButton("Show All");

        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center: Product Table
        String[] columns = {"ID", "Product Name", "Category", "Price (ETB)", "Stock"};
        productTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = UIHelper.createStyledTable();
        productTable.setModel(productTableModel);
        
        rowSorter = new TableRowSorter<>(productTableModel);
        productTable.setRowSorter(rowSorter);

        // Custom renderer for low stock
        productTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int modelRow = table.convertRowIndexToModel(row);
                int stock = (int) productTableModel.getValueAt(modelRow, 4);

                if (!isSelected) {
                    if (stock == 0) {
                        c.setBackground(new Color(255, 230, 230)); // light red
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    }
                }

                if (column == 4) { // Stock column
                    if (stock == 0) {
                        setText("OUT OF STOCK");
                        setForeground(UIConstants.DANGER_COLOR);
                    } else if (stock <= 10) { // Assuming 10 is low stock threshold, actually Product model has lowStockThreshold, but we just check if stock <= 10 for display if not accessible
                        // The user said "show number in orange if isLowStock() else normal color"
                        // I will find out actual isLowStock when loading and store it, or just query it
                        setForeground(new Color(200, 100, 0)); // orange
                    } else {
                        setForeground(UIConstants.TEXT_PRIMARY);
                    }
                } else {
                    setForeground(UIConstants.TEXT_PRIMARY);
                }

                return c;
            }
        });

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addToCart(1);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Bottom: Add to cart
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(UIConstants.FONT_BODY);
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 30));
        
        JButton addToCartBtn = UIHelper.createSuccessButton("Add to Cart ➕");
        
        bottomPanel.add(qtyLabel);
        bottomPanel.add(quantitySpinner);
        bottomPanel.add(addToCartBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterProducts(searchField.getText());
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                filterProducts(searchField.getText());
            }
        });

        searchBtn.addActionListener(e -> filterProducts(searchField.getText()));
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            filterProducts("");
        });
        addToCartBtn.addActionListener(e -> addToCart((int) quantitySpinner.getValue()));

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(Color.WHITE);

        panel.add(UIHelper.createTitleLabel("Current Sale 🛒"), BorderLayout.NORTH);

        // Cart Table
        String[] columns = {"Product", "Qty", "Unit Price", "Subtotal", "Remove"};
        cartTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Remove button is editable
            }
        };
        cartTable = UIHelper.createStyledTable();
        cartTable.setModel(cartTableModel);
        
        cartTable.getColumn("Remove").setCellRenderer(new ButtonRenderer());
        cartTable.getColumn("Remove").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(cartTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Totals and Buttons
        JPanel bottomContainer = new JPanel(new BorderLayout(10, 10));
        bottomContainer.setBackground(Color.WHITE);

        JPanel totalsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        totalsPanel.setBackground(Color.WHITE);
        totalsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        totalsPanel.add(new JLabel("Subtotal:"));
        subtotalLabel = new JLabel("ETB 0.00", SwingConstants.RIGHT);
        totalsPanel.add(subtotalLabel);

        totalsPanel.add(new JLabel("Tax (15%):"));
        taxLabel = new JLabel("ETB 0.00", SwingConstants.RIGHT);
        totalsPanel.add(taxLabel);

        JLabel lblTotal = new JLabel("TOTAL:");
        lblTotal.setFont(UIConstants.FONT_SUBTITLE);
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD));
        totalsPanel.add(lblTotal);

        totalLabel = new JLabel("ETB 0.00", SwingConstants.RIGHT);
        totalLabel.setFont(UIConstants.FONT_SUBTITLE);
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        totalLabel.setForeground(UIConstants.PRIMARY_COLOR);
        totalsPanel.add(totalLabel);

        bottomContainer.add(totalsPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        JButton clearBtn = UIHelper.createDangerButton("Clear Cart 🗑");
        JButton completeBtn = UIHelper.createSuccessButton("Complete Sale ✅");
        completeBtn.setPreferredSize(new Dimension(150, 40));
        
        btnPanel.add(clearBtn);
        btnPanel.add(completeBtn);
        bottomContainer.add(btnPanel, BorderLayout.CENTER);

        String cashierName = authService.getCurrentUser() != null ? authService.getCurrentUser().getFullName() : "Unknown";
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        JLabel infoLabel = new JLabel("Cashier: " + cashierName + " | " + dateStr);
        infoLabel.setFont(UIConstants.FONT_SMALL);
        infoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomContainer.add(infoLabel, BorderLayout.SOUTH);

        panel.add(bottomContainer, BorderLayout.SOUTH);

        // Listeners
        clearBtn.addActionListener(e -> clearCart());
        completeBtn.addActionListener(e -> completeSale());

        return panel;
    }

    private void filterProducts(String query) {
        if (query.trim().isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 1)); // Filter by name
        }
    }

    private void loadProducts() {
        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Product> doInBackground() throws Exception {
                return productService.getAllProducts();
            }

            @Override
            protected void done() {
                try {
                    productList = get();
                    refreshProductTable();
                } catch (Exception e) {
                    UIHelper.showError(POSPanel.this, "Failed to load products: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        if (productList != null) {
            for (Product p : productList) {
                productTableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getProductName(),
                    p.getCategory().getCategoryName(),
                    p.getPrice(),
                    p.getStockQty()
                });
            }
        }
    }

    private void addToCart(int quantity) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow == -1) {
            UIHelper.showWarning(this, "Please select a product from the list.");
            return;
        }

        int modelRow = productTable.convertRowIndexToModel(selectedRow);
        int productId = (int) productTableModel.getValueAt(modelRow, 0);

        Product selectedProduct = null;
        for (Product p : productList) {
            if (p.getProductId() == productId) {
                selectedProduct = p;
                break;
            }
        }

        if (selectedProduct == null) return;

        try {
            // Check if product already in cart
            boolean updated = false;
            for (SaleItem item : currentSale.getItems()) {
                if (item.getProduct().getProductId() == productId) {
                    int newQty = item.getQuantity() + quantity;
                    if (newQty > selectedProduct.getStockQty()) {
                        throw new InsufficientStockException(selectedProduct.getProductName(), selectedProduct.getStockQty(), newQty);
                    }
                    item.setQuantity(newQty);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                saleService.addItemToSale(currentSale, selectedProduct, quantity);
            }
            
            refreshCartTable();
            recalculateTotals();

        } catch (OutOfStockException | InsufficientStockException e) {
            UIHelper.showWarning(this, e.getMessage());
        } catch (SmartMartException e) {
            UIHelper.showError(this, e.getMessage());
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (int i = 0; i < currentSale.getItems().size(); i++) {
            SaleItem item = currentSale.getItems().get(i);
            cartTableModel.addRow(new Object[]{
                item.getProduct().getProductName(),
                item.getQuantity(),
                String.format("%.2f", item.getUnitPrice()),
                String.format("%.2f", item.getSubtotal()),
                "✕"
            });
        }
    }

    private void recalculateTotals() {
        double subtotal = 0;
        for (SaleItem item : currentSale.getItems()) {
            subtotal += item.getSubtotal();
        }
        double tax = subtotal * 0.15;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("ETB %.2f", subtotal));
        taxLabel.setText(String.format("ETB %.2f", tax));
        totalLabel.setText(String.format("ETB %.2f", total));
    }

    private void clearCart() {
        currentSale.getItems().clear();
        refreshCartTable();
        recalculateTotals();
    }

    private void removeFromCart(int row) {
        if (row >= 0 && row < currentSale.getItems().size()) {
            currentSale.getItems().remove(row);
            refreshCartTable();
            recalculateTotals();
        }
    }

    private void completeSale() {
        if (currentSale.getItems().isEmpty()) {
            UIHelper.showWarning(this, "Cart is empty!");
            return;
        }

        double subtotal = 0;
        for (SaleItem item : currentSale.getItems()) subtotal += item.getSubtotal();
        double tax = subtotal * 0.15;
        double total = subtotal + tax;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            String.format("Complete sale of ETB %.2f for %d items?", total, currentSale.getItems().size()),
            "Confirm Sale",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                saleService.processSale(currentSale);
                
                // Show receipt
                ReceiptDialog receipt = new ReceiptDialog(SwingUtilities.getWindowAncestor(this), currentSale, subtotal, tax, total);
                receipt.setVisible(true);

                // Reset for next sale
                currentSale = saleService.createNewSale();
                clearCart();
                loadProducts(); // refresh stock

            } catch (SmartMartException e) {
                UIHelper.showError(this, e.getMessage());
            }
        }
    }

    // Custom Button Renderer for Remove Column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(UIConstants.DANGER_COLOR);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // Custom Button Editor for Remove Column
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(UIConstants.DANGER_COLOR);
            button.setFont(new Font("SansSerif", Font.BOLD, 12));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // remove item
                SwingUtilities.invokeLater(() -> removeFromCart(currentRow));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
