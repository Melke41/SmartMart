package smartmart.ui.admin;

import smartmart.service.AuthService;
import smartmart.service.EmployeeService;
import smartmart.service.ProductService;
import smartmart.service.SaleService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdminOverviewPanel extends JPanel {
    private final JLabel lblTotalProducts;
    private final JLabel lblTodayRevenue;
    private final JLabel lblTotalEmployees;
    private final JLabel lblLowStock;
    private final JLabel lblBottomInfo;

    private final ProductService productService;
    private final SaleService saleService;
    private final EmployeeService employeeService;

    public AdminOverviewPanel() {
        this.productService = new ProductService();
        this.saleService = new SaleService();
        this.employeeService = new EmployeeService();

        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createTitleLabel("Admin Overview");
        JButton btnRefresh = UIHelper.createPrimaryButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStatistics();
            }
        });

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Center Grid (2x2 Grid of Stat Cards)
        JPanel gridPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        gridPanel.setOpaque(false);

        // Card 1: Total Products
        JPanel cardProducts = UIHelper.createCard();
        cardProducts.setLayout(new BoxLayout(cardProducts, BoxLayout.Y_AXIS));
        JLabel iconProducts = new JLabel("📦");
        iconProducts.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconProducts.setForeground(UIConstants.PRIMARY_COLOR);
        iconProducts.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTotalProducts = new JLabel("Loading...", SwingConstants.CENTER);
        lblTotalProducts.setFont(UIConstants.FONT_TITLE);
        lblTotalProducts.setForeground(UIConstants.TEXT_PRIMARY);
        lblTotalProducts.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descProducts = new JLabel("Total Registered Products", SwingConstants.CENTER);
        descProducts.setFont(UIConstants.FONT_SMALL);
        descProducts.setForeground(UIConstants.TEXT_SECONDARY);
        descProducts.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardProducts.add(Box.createVerticalGlue());
        cardProducts.add(iconProducts);
        cardProducts.add(Box.createRigidArea(new Dimension(0, 10)));
        cardProducts.add(lblTotalProducts);
        cardProducts.add(Box.createRigidArea(new Dimension(0, 5)));
        cardProducts.add(descProducts);
        cardProducts.add(Box.createVerticalGlue());

        // Card 2: Today's Revenue
        JPanel cardRevenue = UIHelper.createCard();
        cardRevenue.setLayout(new BoxLayout(cardRevenue, BoxLayout.Y_AXIS));
        JLabel iconRevenue = new JLabel("💵");
        iconRevenue.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconRevenue.setForeground(UIConstants.SECONDARY_COLOR);
        iconRevenue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTodayRevenue = new JLabel("Loading...", SwingConstants.CENTER);
        lblTodayRevenue.setFont(UIConstants.FONT_TITLE);
        lblTodayRevenue.setForeground(UIConstants.SECONDARY_COLOR);
        lblTodayRevenue.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descRevenue = new JLabel("Today's Total Sales Revenue", SwingConstants.CENTER);
        descRevenue.setFont(UIConstants.FONT_SMALL);
        descRevenue.setForeground(UIConstants.TEXT_SECONDARY);
        descRevenue.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardRevenue.add(Box.createVerticalGlue());
        cardRevenue.add(iconRevenue);
        cardRevenue.add(Box.createRigidArea(new Dimension(0, 10)));
        cardRevenue.add(lblTodayRevenue);
        cardRevenue.add(Box.createRigidArea(new Dimension(0, 5)));
        cardRevenue.add(descRevenue);
        cardRevenue.add(Box.createVerticalGlue());

        // Card 3: Total Employees
        JPanel cardEmployees = UIHelper.createCard();
        cardEmployees.setLayout(new BoxLayout(cardEmployees, BoxLayout.Y_AXIS));
        JLabel iconEmployees = new JLabel("👥");
        iconEmployees.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconEmployees.setForeground(UIConstants.TEXT_PRIMARY);
        iconEmployees.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTotalEmployees = new JLabel("Loading...", SwingConstants.CENTER);
        lblTotalEmployees.setFont(UIConstants.FONT_TITLE);
        lblTotalEmployees.setForeground(UIConstants.TEXT_PRIMARY);
        lblTotalEmployees.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descEmployees = new JLabel("Total Active Employees", SwingConstants.CENTER);
        descEmployees.setFont(UIConstants.FONT_SMALL);
        descEmployees.setForeground(UIConstants.TEXT_SECONDARY);
        descEmployees.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardEmployees.add(Box.createVerticalGlue());
        cardEmployees.add(iconEmployees);
        cardEmployees.add(Box.createRigidArea(new Dimension(0, 10)));
        cardEmployees.add(lblTotalEmployees);
        cardEmployees.add(Box.createRigidArea(new Dimension(0, 5)));
        cardEmployees.add(descEmployees);
        cardEmployees.add(Box.createVerticalGlue());

        // Card 4: Low Stock Alerts
        JPanel cardStock = UIHelper.createCard();
        cardStock.setLayout(new BoxLayout(cardStock, BoxLayout.Y_AXIS));
        JLabel iconStock = new JLabel("⚠️");
        iconStock.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        iconStock.setForeground(UIConstants.WARNING_COLOR);
        iconStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLowStock = new JLabel("Loading...", SwingConstants.CENTER);
        lblLowStock.setFont(UIConstants.FONT_TITLE);
        lblLowStock.setForeground(UIConstants.WARNING_COLOR);
        lblLowStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel descStock = new JLabel("Low Stock Threshold Items", SwingConstants.CENTER);
        descStock.setFont(UIConstants.FONT_SMALL);
        descStock.setForeground(UIConstants.TEXT_SECONDARY);
        descStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardStock.add(Box.createVerticalGlue());
        cardStock.add(iconStock);
        cardStock.add(Box.createRigidArea(new Dimension(0, 10)));
        cardStock.add(lblLowStock);
        cardStock.add(Box.createRigidArea(new Dimension(0, 5)));
        cardStock.add(descStock);
        cardStock.add(Box.createVerticalGlue());

        gridPanel.add(cardProducts);
        gridPanel.add(cardRevenue);
        gridPanel.add(cardEmployees);
        gridPanel.add(cardStock);
        add(gridPanel, BorderLayout.CENTER);

        // Bottom Session Details
        lblBottomInfo = new JLabel("", SwingConstants.RIGHT);
        lblBottomInfo.setFont(UIConstants.FONT_SMALL);
        lblBottomInfo.setForeground(UIConstants.TEXT_SECONDARY);
        add(lblBottomInfo, BorderLayout.SOUTH);

        // Load stats initially
        loadStatistics();
    }

    private void loadStatistics() {
        lblTotalProducts.setText("Loading...");
        lblTodayRevenue.setText("Loading...");
        lblTotalEmployees.setText("Loading...");
        lblLowStock.setText("Loading...");

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int totalProducts = 0;
            private double todayRevenue = 0.0;
            private int totalEmployees = 0;
            private int lowStockCount = 0;
            private String userSession = "";

            @Override
            protected Void doInBackground() throws Exception {
                totalProducts = productService.getAllProducts().size();
                todayRevenue = saleService.getTotalRevenueToday();
                totalEmployees = employeeService.getAllEmployees().size();
                lowStockCount = productService.getLowStockProducts().size();

                String username = AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getFullName() : "Administrator";
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                userSession = "Logged in as: " + username + " | " + timestamp;
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Throw exception if task failed
                    lblTotalProducts.setText(String.valueOf(totalProducts));
                    lblTodayRevenue.setText(String.format("ETB %,.2f", todayRevenue));
                    lblTotalEmployees.setText(String.valueOf(totalEmployees));
                    lblLowStock.setText(String.valueOf(lowStockCount));

                    if (lowStockCount > 0) {
                        lblLowStock.setForeground(UIConstants.DANGER_COLOR);
                    } else {
                        lblLowStock.setForeground(UIConstants.SECONDARY_COLOR);
                    }
                    lblBottomInfo.setText(userSession);
                } catch (Exception e) {
                    lblTotalProducts.setText("Error");
                    lblTodayRevenue.setText("Error");
                    lblTotalEmployees.setText("Error");
                    lblLowStock.setText("Error");
                    lblBottomInfo.setText("Error loading statistics: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }
}
