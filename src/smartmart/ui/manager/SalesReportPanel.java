package smartmart.ui.manager;

import smartmart.model.Product;
import smartmart.model.Sale;
import smartmart.model.SaleItem;
import smartmart.service.ReportService;
import smartmart.service.SaleService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesReportPanel extends JPanel {
    private final SaleService saleService;
    private final ReportService reportService;

    private JTextField fromDateField;
    private JTextField toDateField;
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTable itemsTable;
    private DefaultTableModel itemsModel;

    private JLabel lblTotalSales;
    private JLabel lblTotalRevenue;
    private JLabel lblAvgSale;
    private JLabel lblBestProduct;

    private List<Sale> currentSales;

    public SalesReportPanel() {
        saleService = new SaleService();
        reportService = new ReportService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(UIConstants.BACKGROUND_COLOR);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        toolbar.add(UIHelper.createTitleLabel("Sales Report"));
        toolbar.add(new JLabel("From:"));
        
        fromDateField = UIHelper.createStyledTextField();
        fromDateField.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        fromDateField.setText(sdf.format(cal.getTime()));
        toolbar.add(fromDateField);

        toolbar.add(new JLabel("To:"));
        toDateField = UIHelper.createStyledTextField();
        toDateField.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));
        toDateField.setText(sdf.format(new Date()));
        toolbar.add(toDateField);

        JButton btnGenerate = UIHelper.createPrimaryButton("Generate Report");
        btnGenerate.addActionListener(e -> generateReport());
        toolbar.add(btnGenerate);

        JButton btnExport = UIHelper.createSecondaryButton("Export to File");
        btnExport.addActionListener(e -> exportReport());
        toolbar.add(btnExport);

        add(toolbar, BorderLayout.NORTH);

        // Center Area (Split Pane)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(0.6);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        // Top - Sales Table
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        topPanel.setBackground(Color.WHITE);
        
        String[] salesCols = {"Sale ID", "Date", "Time", "Cashier", "Items Count", "Total (ETB)"};
        salesModel = new DefaultTableModel(salesCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        salesTable = UIHelper.createStyledTable();
        salesTable.setModel(salesModel);
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salesTable.getSelectedRow();
                if (row >= 0 && currentSales != null && row < currentSales.size()) {
                    loadSaleItems(currentSales.get(row));
                }
            }
        });
        topPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        // Bottom - Sale Items Table
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(UIHelper.createSubtitleLabel("Sale Items"), BorderLayout.NORTH);

        String[] itemCols = {"Product", "Category", "Qty", "Unit Price", "Subtotal"};
        itemsModel = new DefaultTableModel(itemCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = UIHelper.createStyledTable();
        itemsTable.setModel(itemsModel);
        bottomPanel.add(new JScrollPane(itemsTable), BorderLayout.CENTER);

        splitPane.setTopComponent(topPanel);
        splitPane.setBottomComponent(bottomPanel);
        add(splitPane, BorderLayout.CENTER);

        // Right Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(UIConstants.CARD_COLOR);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 225)),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));

        sidebar.add(UIHelper.createSubtitleLabel("Summary"));
        sidebar.add(Box.createVerticalStrut(20));

        lblTotalSales = new JLabel("0");
        lblTotalSales.setFont(UIConstants.FONT_TITLE);
        sidebar.add(createSummaryCard("Total Sales", lblTotalSales));
        sidebar.add(Box.createVerticalStrut(15));

        lblTotalRevenue = new JLabel("ETB 0.00");
        lblTotalRevenue.setFont(UIConstants.FONT_TITLE);
        lblTotalRevenue.setForeground(UIConstants.SECONDARY_COLOR);
        sidebar.add(createSummaryCard("Total Revenue", lblTotalRevenue));
        sidebar.add(Box.createVerticalStrut(15));

        lblAvgSale = new JLabel("ETB 0.00");
        lblAvgSale.setFont(UIConstants.FONT_TITLE);
        sidebar.add(createSummaryCard("Average Sale Value", lblAvgSale));
        sidebar.add(Box.createVerticalStrut(15));

        lblBestProduct = new JLabel("-");
        lblBestProduct.setFont(UIConstants.FONT_SUBTITLE);
        sidebar.add(createSummaryCard("Best Selling Product", lblBestProduct));

        add(sidebar, BorderLayout.EAST);
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UIConstants.CARD_COLOR);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_SMALL);
        lblTitle.setForeground(UIConstants.TEXT_SECONDARY);
        
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(10));
        
        JSplitPane separator = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        separator.setPreferredSize(new Dimension(180, 1));
        separator.setEnabled(false);
        panel.add(separator);

        return panel;
    }

    private void generateReport() {
        String from = fromDateField.getText().trim();
        String to = toDateField.getText().trim();
        if (from.isEmpty() || to.isEmpty()) {
            UIHelper.showError(this, "Please enter both From and To dates.");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            List<Sale> sales;
            double revenue = 0;
            String bestProduct = "-";

            @Override
            protected Void doInBackground() throws Exception {
                sales = saleService.getSalesByDateRange(from, to);
                
                // Calculate best product locally for the given range
                java.util.Map<String, Integer> productCounts = new java.util.HashMap<>();
                for (Sale s : sales) {
                    revenue += s.getTotalAmount();
                    for (SaleItem item : s.getItems()) {
                        String pName = item.getProduct() != null ? item.getProduct().getProductName() : "Unknown";
                        productCounts.put(pName, productCounts.getOrDefault(pName, 0) + item.getQuantity());
                    }
                }
                
                int maxQty = 0;
                for (java.util.Map.Entry<String, Integer> entry : productCounts.entrySet()) {
                    if (entry.getValue() > maxQty) {
                        maxQty = entry.getValue();
                        bestProduct = entry.getKey() + " (" + maxQty + " units)";
                    }
                }
                
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    currentSales = sales;
                    salesModel.setRowCount(0);
                    itemsModel.setRowCount(0);

                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);

                    SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm:ss");

                    for (Sale s : sales) {
                        String date = "N/A";
                        String time = "N/A";
                        try {
                            Date d = dtFormat.parse(s.getSaleDate());
                            date = dFormat.format(d);
                            time = tFormat.format(d);
                        } catch (Exception ignored) {}
                        
                        String cashier = s.getCashier() != null ? s.getCashier().getFullName() : "N/A";
                        int itemsCount = s.getItems().size();

                        salesModel.addRow(new Object[]{
                                s.getSaleId(),
                                date,
                                time,
                                cashier,
                                itemsCount,
                                nf.format(s.getTotalAmount())
                        });
                    }

                    // Update sidebar
                    lblTotalSales.setText(String.valueOf(sales.size()));
                    lblTotalRevenue.setText("ETB " + nf.format(revenue));
                    
                    double avg = sales.size() > 0 ? revenue / sales.size() : 0;
                    lblAvgSale.setText("ETB " + nf.format(avg));
                    lblBestProduct.setText(bestProduct);

                } catch (Exception e) {
                    e.printStackTrace();
                    UIHelper.showError(SalesReportPanel.this, "Failed to load sales report.");
                }
            }
        };
        worker.execute();
    }

    private void loadSaleItems(Sale sale) {
        itemsModel.setRowCount(0);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        for (SaleItem item : sale.getItems()) {
            Product p = item.getProduct();
            String pName = p != null ? p.getProductName() : "Unknown";
            String cName = p != null && p.getCategory() != null ? p.getCategory().getCategoryName() : "N/A";

            itemsModel.addRow(new Object[]{
                    pName,
                    cName,
                    item.getQuantity(),
                    nf.format(item.getUnitPrice()),
                    nf.format(item.getSubtotal())
            });
        }
    }

    private void exportReport() {
        String from = fromDateField.getText().trim();
        if (from.isEmpty()) {
            UIHelper.showError(this, "Please enter at least a From date.");
            return;
        }
        
        try {
            // Generating report content for the 'from' date as an example
            // In a real app, ReportService might need updating to handle date ranges
            String reportText = reportService.generateSalesReport(from);
            String fileName = "sales_report_" + from + ".txt";
            boolean success = reportService.saveReportToFile(reportText, fileName);
            if (success) {
                UIHelper.showSuccess(this, "Report saved successfully to docs/" + fileName);
            } else {
                UIHelper.showError(this, "Failed to save report.");
            }
        } catch (Exception e) {
            UIHelper.showError(this, "Error exporting report: " + e.getMessage());
        }
    }
}
