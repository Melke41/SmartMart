package smartmart.ui.manager;

import smartmart.model.Product;
import smartmart.service.ProductService;
import smartmart.service.ReportService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InventoryReportPanel extends JPanel {
    private final ProductService productService;
    private final ReportService reportService;

    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    private JTable categoryTable;
    private DefaultTableModel categoryModel;

    private JComboBox<String> filterCombo;
    
    private JLabel lblSummary;

    public InventoryReportPanel() {
        productService = new ProductService();
        reportService = new ReportService();
        initUI();
        generateReport();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(UIConstants.BACKGROUND_COLOR);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        toolbar.add(UIHelper.createTitleLabel("Inventory Report"));
        
        JButton btnGenerate = UIHelper.createPrimaryButton("Generate Report");
        btnGenerate.addActionListener(e -> generateReport());
        toolbar.add(btnGenerate);

        JButton btnExport = UIHelper.createSecondaryButton("Export to File");
        btnExport.addActionListener(e -> exportReport());
        toolbar.add(btnExport);

        toolbar.add(new JLabel("Filter:"));
        filterCombo = new JComboBox<>(new String[]{"All Products", "Low Stock Only", "Out of Stock"});
        filterCombo.setFont(UIConstants.FONT_BODY);
        filterCombo.setPreferredSize(new Dimension(150, UIConstants.FORM_FIELD_HEIGHT));
        filterCombo.addActionListener(e -> applyFilter());
        toolbar.add(filterCombo);

        add(toolbar, BorderLayout.NORTH);

        // Center Area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        // Left - Inventory Table
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        leftPanel.setBackground(Color.WHITE);

        String[] invCols = {"ID", "Product Name", "Category", "Supplier", "Price", "Stock", "Low Stock Limit", "Status"};
        inventoryModel = new DefaultTableModel(invCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = UIHelper.createStyledTable();
        inventoryTable.setModel(inventoryModel);
        
        rowSorter = new TableRowSorter<>(inventoryModel);
        inventoryTable.setRowSorter(rowSorter);

        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int modelRow = table.convertRowIndexToModel(row);
                int stock = (int) inventoryModel.getValueAt(modelRow, 5);
                int limit = (int) inventoryModel.getValueAt(modelRow, 6);
                
                if (!isSelected) {
                    if (stock == 0) {
                        c.setBackground(new Color(255, 220, 220)); // Light red
                    } else if (stock <= limit) {
                        c.setBackground(UIConstants.LOW_STOCK_COLOR); // Light yellow
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    }
                }

                if (column == 7) { // Status column color
                    if (stock == 0) c.setForeground(UIConstants.DANGER_COLOR);
                    else if (stock <= limit) c.setForeground(UIConstants.WARNING_COLOR);
                    else c.setForeground(new Color(0, 153, 0)); // Green
                } else {
                    c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                }

                return c;
            }
        });

        leftPanel.add(new JScrollPane(inventoryTable), BorderLayout.CENTER);

        // Right - Category Breakdown
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.add(UIHelper.createSubtitleLabel("Stock by Category"), BorderLayout.NORTH);

        String[] catCols = {"Category", "Products", "Total Stock", "Low Stock Count"};
        categoryModel = new DefaultTableModel(catCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = UIHelper.createStyledTable();
        categoryTable.setModel(categoryModel);
        
        categoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                int lowStockCount = (int) table.getValueAt(row, 3);
                if (!isSelected) {
                    if (lowStockCount > 0) {
                        c.setBackground(new Color(255, 235, 204)); // Orange tint
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                    }
                }
                return c;
            }
        });

        rightPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Bottom Summary Bar
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        summaryPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        summaryPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        
        lblSummary = new JLabel("Summary Loading...");
        lblSummary.setFont(UIConstants.FONT_BUTTON);
        summaryPanel.add(lblSummary);
        
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void applyFilter() {
        String filter = (String) filterCombo.getSelectedItem();
        if ("All Products".equals(filter)) {
            rowSorter.setRowFilter(null);
        } else if ("Low Stock Only".equals(filter)) {
            rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    int stock = (int) entry.getValue(5);
                    int limit = (int) entry.getValue(6);
                    return stock > 0 && stock <= limit;
                }
            });
        } else if ("Out of Stock".equals(filter)) {
            rowSorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                @Override
                public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                    int stock = (int) entry.getValue(5);
                    return stock == 0;
                }
            });
        }
    }

    private void generateReport() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            List<Product> products;

            @Override
            protected Void doInBackground() throws Exception {
                products = productService.getAllProducts();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    inventoryModel.setRowCount(0);
                    categoryModel.setRowCount(0);

                    double totalValue = 0;
                    int lowStockCount = 0;
                    int outOfStockCount = 0;

                    Map<String, int[]> categoryStats = new HashMap<>(); // [productsCount, totalStock, lowStockCount]

                    for (Product p : products) {
                        int stock = p.getStockQty();
                        int limit = p.getLowStockLimit();
                        String statusStr;
                        
                        if (stock == 0) {
                            statusStr = "❌ Out of Stock";
                            outOfStockCount++;
                        } else if (stock <= limit) {
                            statusStr = "⚠ Low Stock";
                            lowStockCount++;
                        } else {
                            statusStr = "✅ OK";
                        }

                        totalValue += (p.getPrice() * stock);

                        String catName = p.getCategory() != null ? p.getCategory().getCategoryName() : "Uncategorized";
                        
                        int[] stats = categoryStats.computeIfAbsent(catName, k -> new int[3]);
                        stats[0]++; // productsCount
                        stats[1] += stock; // totalStock
                        if (stock <= limit) {
                            stats[2]++; // lowStockCount
                        }

                        inventoryModel.addRow(new Object[]{
                                p.getProductId(),
                                p.getProductName(),
                                catName,
                                p.getSupplier() != null ? p.getSupplier().getName() : "N/A",
                                p.getPrice(),
                                stock,
                                limit,
                                statusStr
                        });
                    }

                    // Populate category table
                    for (Map.Entry<String, int[]> entry : categoryStats.entrySet()) {
                        int[] stats = entry.getValue();
                        categoryModel.addRow(new Object[]{
                                entry.getKey(),
                                stats[0],
                                stats[1],
                                stats[2]
                        });
                    }

                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);

                    String summaryText = String.format("Total Products: %d   |   Total Stock Value: ETB %s   |   Low Stock: %d   |   Out of Stock: %d",
                            products.size(), nf.format(totalValue), lowStockCount, outOfStockCount);
                    lblSummary.setText(summaryText);
                    
                    applyFilter();

                } catch (Exception e) {
                    e.printStackTrace();
                    UIHelper.showError(InventoryReportPanel.this, "Failed to load inventory report.");
                }
            }
        };
        worker.execute();
    }

    private void exportReport() {
        try {
            String reportText = reportService.generateInventoryReport();
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String fileName = "inventory_report_" + dateStr + ".txt";
            boolean success = reportService.saveReportToFile(reportText, fileName);
            if (success) {
                UIHelper.showSuccess(this, "Report saved successfully to docs/" + fileName);
            } else {
                UIHelper.showError(this, "Failed to save inventory report.");
            }
        } catch (Exception e) {
            UIHelper.showError(this, "Error exporting report: " + e.getMessage());
        }
    }
}
