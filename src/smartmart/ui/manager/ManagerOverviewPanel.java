package smartmart.ui.manager;

import smartmart.model.Sale;
import smartmart.service.ProductService;
import smartmart.service.SaleService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagerOverviewPanel extends JPanel {
    private final SaleService saleService;
    private final ProductService productService;

    private JLabel lblTime;
    private JLabel lblRevenue;
    private JLabel lblTransactions;
    private JLabel lblLowStock;
    private JLabel lblTotalProducts;

    private JTable topProductsTable;
    private DefaultTableModel topProductsModel;
    private JList<String> recentSalesList;
    private DefaultListModel<String> recentSalesModel;

    private JLabel lblLoading;

    public ManagerOverviewPanel() {
        this.saleService = new SaleService();
        this.productService = new ProductService();
        initUI();
        loadData();
        startTimers();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Row
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(UIHelper.createTitleLabel("Manager Dashboard"), BorderLayout.WEST);

        lblTime = new JLabel();
        lblTime.setFont(UIConstants.FONT_SUBTITLE);
        lblTime.setForeground(UIConstants.TEXT_SECONDARY);
        topPanel.add(lblTime, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Top Stats Row
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblRevenue = new JLabel("ETB 0.00", SwingConstants.CENTER);
        lblRevenue.setFont(UIConstants.FONT_TITLE);
        lblRevenue.setForeground(UIConstants.SECONDARY_COLOR);
        statsPanel.add(createStatCard("💰", "Total Revenue Today", lblRevenue));

        lblTransactions = new JLabel("0", SwingConstants.CENTER);
        lblTransactions.setFont(UIConstants.FONT_TITLE);
        lblTransactions.setForeground(UIConstants.PRIMARY_COLOR);
        statsPanel.add(createStatCard("🧾", "Sales Completed", lblTransactions));

        lblLowStock = new JLabel("0", SwingConstants.CENTER);
        lblLowStock.setFont(UIConstants.FONT_TITLE);
        lblLowStock.setForeground(UIConstants.SECONDARY_COLOR);
        statsPanel.add(createStatCard("⚠", "Products Need Restock", lblLowStock));

        lblTotalProducts = new JLabel("0", SwingConstants.CENTER);
        lblTotalProducts.setFont(UIConstants.FONT_TITLE);
        lblTotalProducts.setForeground(UIConstants.PRIMARY_COLOR);
        statsPanel.add(createStatCard("📦", "Active Products", lblTotalProducts));

        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(20));

        // Bottom Section (Split Pane)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.55);
        splitPane.setResizeWeight(0.55);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        // Left - Top Selling
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(UIHelper.createSubtitleLabel("🏆 Top Selling Products Today"), BorderLayout.NORTH);

        String[] topCols = {"Rank", "Product Name", "Units Sold"};
        topProductsModel = new DefaultTableModel(topCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        topProductsTable = UIHelper.createStyledTable();
        topProductsTable.setModel(topProductsModel);

        topProductsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row == 0) c.setBackground(new Color(255, 243, 176)); // Gold
                    else if (row == 1) c.setBackground(new Color(220, 220, 220)); // Silver
                    else if (row == 2) c.setBackground(new Color(255, 220, 185)); // Bronze
                    else c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                return c;
            }
        });
        
        JScrollPane topScroll = new JScrollPane(topProductsTable);
        topScroll.getViewport().setBackground(Color.WHITE);
        leftPanel.add(topScroll, BorderLayout.CENTER);

        // Right - Recent Sales
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        rightPanel.add(UIHelper.createSubtitleLabel("🕐 Recent Sales"), BorderLayout.NORTH);

        recentSalesModel = new DefaultListModel<>();
        recentSalesList = new JList<>(recentSalesModel);
        recentSalesList.setFont(UIConstants.FONT_BODY);
        recentSalesList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (!isSelected) {
                    c.setBackground(index % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                return c;
            }
        });

        JScrollPane recentScroll = new JScrollPane(recentSalesList);
        recentScroll.getViewport().setBackground(Color.WHITE);
        rightPanel.add(recentScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        centerPanel.add(splitPane);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Bar
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        JButton btnRefresh = UIHelper.createPrimaryButton("Refresh Dashboard");
        btnRefresh.addActionListener(e -> loadData());
        
        lblLoading = new JLabel("Loading...");
        lblLoading.setFont(UIConstants.FONT_SMALL);
        lblLoading.setForeground(UIConstants.TEXT_SECONDARY);
        lblLoading.setVisible(false);

        bottomPanel.add(btnRefresh);
        bottomPanel.add(lblLoading);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatCard(String iconStr, String title, JLabel valueLabel) {
        JPanel card = UIHelper.createCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel(iconStr);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(UIConstants.FONT_SMALL);
        lblTitle.setForeground(UIConstants.TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(icon);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(lblTitle);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private void loadData() {
        lblLoading.setVisible(true);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            double revenue = 0;
            int transactions = 0;
            int lowStock = 0;
            int totalProducts = 0;
            List<Object[]> topProducts;
            List<Sale> allSales;

            @Override
            protected Void doInBackground() throws Exception {
                revenue = saleService.getTotalRevenueToday();
                transactions = saleService.getTotalTransactionsToday();
                lowStock = productService.getLowStockProducts().size();
                totalProducts = productService.getAllProducts().size();
                topProducts = saleService.getTopSellingProducts(5);
                
                String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                allSales = saleService.getSalesByDate(today);
                return null;
            }

            @Override
            protected void done() {
                lblLoading.setVisible(false);
                try {
                    get(); // Check for exceptions

                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(2);
                    
                    lblRevenue.setText("ETB " + nf.format(revenue));
                    lblTransactions.setText(String.valueOf(transactions));
                    
                    lblLowStock.setText(String.valueOf(lowStock));
                    if (lowStock > 0) {
                        lblLowStock.setForeground(UIConstants.WARNING_COLOR);
                    } else {
                        lblLowStock.setForeground(UIConstants.SECONDARY_COLOR);
                    }
                    
                    lblTotalProducts.setText(String.valueOf(totalProducts));

                    // Top products
                    topProductsModel.setRowCount(0);
                    int rank = 1;
                    if (topProducts != null) {
                        for (Object[] row : topProducts) {
                            topProductsModel.addRow(new Object[]{rank++, row[0], row[1]});
                        }
                    }

                    // Recent sales
                    recentSalesModel.clear();
                    if (allSales != null) {
                        Collections.reverse(allSales);
                        int count = 0;
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                        for (Sale s : allSales) {
                            if (count >= 10) break;
                            String time = "N/A";
                            try {
                                Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s.getSaleDate());
                                time = timeFormat.format(d);
                            } catch (Exception ignored) {}
                            
                            String cashier = s.getCashier() != null ? s.getCashier().getFullName() : "Unknown";
                            String formatted = String.format("REC-%03d | %s | ETB %s | %s", 
                                    s.getSaleId(), cashier, nf.format(s.getTotalAmount()), time);
                            recentSalesModel.addElement(formatted);
                            count++;
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    UIHelper.showError(ManagerOverviewPanel.this, "Failed to load dashboard data.");
                }
            }
        };
        worker.execute();
    }

    private void startTimers() {
        Timer clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy  HH:mm:ss");
            lblTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();

        Timer autoRefreshTimer = new Timer(30000, e -> loadData());
        autoRefreshTimer.start();
    }
}
