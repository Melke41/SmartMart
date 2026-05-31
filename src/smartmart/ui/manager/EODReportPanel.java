package smartmart.ui.manager;

import smartmart.service.ReportService;
import smartmart.service.SaleService;
import smartmart.service.ProductService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EODReportPanel extends JPanel {
    private final ReportService reportService;
    private final SaleService saleService;
    private final ProductService productService;

    private JTextField dateField;
    private JTextArea reportArea;
    private JPanel rightPanel;

    private JLabel lblRevenue;
    private JLabel lblTransactions;
    private JLabel lblTopProduct;
    private JLabel lblLowStock;
    private JLabel lblEmployees;

    public EODReportPanel() {
        this.reportService = new ReportService();
        this.saleService = new SaleService();
        this.productService = new ProductService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbar.setBackground(UIConstants.BACKGROUND_COLOR);
        toolbar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        toolbar.add(UIHelper.createTitleLabel("End of Day Report"));
        toolbar.add(new JLabel("Date:"));
        
        dateField = UIHelper.createStyledTextField();
        dateField.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        toolbar.add(dateField);

        JButton btnGenerate = UIHelper.createSuccessButton("Generate EOD Report");
        btnGenerate.addActionListener(e -> generateReport());
        toolbar.add(btnGenerate);

        JButton btnSave = UIHelper.createSecondaryButton("Save Report");
        btnSave.addActionListener(e -> saveReport());
        toolbar.add(btnSave);

        add(toolbar, BorderLayout.NORTH);

        // Center Area (Text Area for Report)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.setBackground(Color.WHITE);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        reportArea.setBackground(new Color(250, 250, 250));
        reportArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        reportArea.setText("Click 'Generate EOD Report' to generate today's end of day summary.");

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Right Quick Stats Panel
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(220, 0));
        rightPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 225)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        rightPanel.setVisible(false); // Shown after report generation

        rightPanel.add(UIHelper.createSubtitleLabel("Quick Stats"));
        rightPanel.add(Box.createVerticalStrut(15));

        lblRevenue = new JLabel("ETB 0.00");
        lblRevenue.setFont(UIConstants.FONT_TITLE);
        lblRevenue.setForeground(UIConstants.SECONDARY_COLOR);
        rightPanel.add(createStatCard("Today's Revenue", lblRevenue));

        lblTransactions = new JLabel("0");
        lblTransactions.setFont(UIConstants.FONT_TITLE);
        rightPanel.add(createStatCard("Total Transactions", lblTransactions));

        lblTopProduct = new JLabel("-");
        lblTopProduct.setFont(UIConstants.FONT_SUBTITLE);
        rightPanel.add(createStatCard("Top Product", lblTopProduct));

        lblLowStock = new JLabel("0");
        lblLowStock.setFont(UIConstants.FONT_TITLE);
        rightPanel.add(createStatCard("Low Stock Count", lblLowStock));

        lblEmployees = new JLabel("0");
        lblEmployees.setFont(UIConstants.FONT_TITLE);
        rightPanel.add(createStatCard("Employees on Shift", lblEmployees)); // Placeholder, could be calculated if we tracked shifts

        add(rightPanel, BorderLayout.EAST);
    }

    private JPanel createStatCard(String title, JLabel valueLabel) {
        JPanel card = UIHelper.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_SMALL);
        lblTitle.setForeground(UIConstants.TEXT_SECONDARY);
        
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);

        return card;
    }

    private void generateReport() {
        reportArea.setText("Generating report, please wait...");
        rightPanel.setVisible(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            String reportText = "";
            double revenue = 0;
            int transactions = 0;
            int lowStock = 0;
            String topProduct = "-";

            @Override
            protected Void doInBackground() throws Exception {
                // Wait to make the UI look like it's doing complex work (optional)
                Thread.sleep(500);
                
                reportText = reportService.generateEODReport();
                
                revenue = saleService.getTotalRevenueToday();
                transactions = saleService.getTotalTransactionsToday();
                lowStock = productService.getLowStockProducts().size();
                
                List<Object[]> top = saleService.getTopSellingProducts(1);
                if (top != null && !top.isEmpty()) {
                    Object[] best = top.get(0);
                    topProduct = best[0] + " (" + best[1] + " units)";
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    reportArea.setText(reportText);
                    
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);

                    lblRevenue.setText("ETB " + nf.format(revenue));
                    lblTransactions.setText(String.valueOf(transactions));
                    lblTopProduct.setText(topProduct);
                    lblLowStock.setText(String.valueOf(lowStock));
                    if (lowStock > 0) {
                        lblLowStock.setForeground(UIConstants.WARNING_COLOR);
                    } else {
                        lblLowStock.setForeground(UIConstants.TEXT_PRIMARY);
                    }
                    lblEmployees.setText("1"); // Simplified for now

                    rightPanel.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    reportArea.setText("Failed to generate EOD report: " + e.getMessage());
                    UIHelper.showError(EODReportPanel.this, "Failed to generate EOD report.");
                }
            }
        };
        worker.execute();
    }

    private void saveReport() {
        String text = reportArea.getText();
        if (text.isEmpty() || text.contains("Click 'Generate EOD Report'")) {
            UIHelper.showError(this, "Please generate a report first.");
            return;
        }

        String dateStr = dateField.getText().trim();
        String fileName = "EOD_report_" + dateStr + ".txt";
        
        boolean success = reportService.saveReportToFile(text, fileName);
        if (success) {
            UIHelper.showSuccess(this, "Report saved successfully to docs/" + fileName);
        } else {
            UIHelper.showError(this, "Failed to save report.");
        }
    }
}
