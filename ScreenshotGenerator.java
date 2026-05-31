import smartmart.model.*;
import smartmart.service.AuthService;
import smartmart.service.ProductService;
import smartmart.service.SaleService;
import smartmart.ui.LoginFrame;
import smartmart.ui.admin.*;
import smartmart.ui.cashier.*;
import smartmart.ui.manager.*;
import smartmart.util.DatabaseConnection;
import smartmart.util.UIConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ScreenshotGenerator {

    public static void main(String[] args) {
        System.out.println("Starting Automated Screenshot Generator...");
        
        // Connect to database
        DatabaseConnection.getInstance();
        
        // Ensure directory exists
        File screenDir = new File("docs/screenshots");
        if (!screenDir.exists()) {
            screenDir.mkdirs();
        }

        try {
            // 1. Capture Login Screen
            System.out.println("Capturing Login Screen...");
            LoginFrame loginFrame = new LoginFrame();
            captureComponent(loginFrame, 900, 550, "login_screen.png");

            // Setup services
            AuthService authService = new AuthService();

            // --- ADMIN SCENES ---
            System.out.println("Logging in as Admin for Admin Panel screens...");
            authService.login("admin", "admin123");

            // 2. Admin Overview
            System.out.println("Capturing Admin Overview...");
            AdminOverviewPanel adminOverview = new AdminOverviewPanel();
            captureComponent(adminOverview, 1000, 640, "admin_overview.png");

            // 3. Admin Products
            System.out.println("Capturing Admin Products...");
            ProductManagementPanel adminProducts = new ProductManagementPanel();
            captureComponent(adminProducts, 1000, 640, "admin_products.png");

            // 4. Admin Add Product Dialog Content
            System.out.println("Capturing Add Product Dialog...");
            AddEditProductDialog addDialog = new AddEditProductDialog(null, adminProducts, null);
            captureComponent(addDialog.getContentPane(), 420, 480, "admin_add_product.png");

            // 5. Admin Employees
            System.out.println("Capturing Admin Employees...");
            EmployeeManagementPanel adminEmployees = new EmployeeManagementPanel();
            captureComponent(adminEmployees, 1000, 640, "admin_employees.png");

            // 6. Admin Suppliers
            System.out.println("Capturing Admin Suppliers...");
            SupplierManagementPanel adminSuppliers = new SupplierManagementPanel();
            captureComponent(adminSuppliers, 1000, 640, "admin_suppliers.png");

            // 7. Admin Restock
            System.out.println("Capturing Admin Restock...");
            RestockOrderPanel adminRestock = new RestockOrderPanel();
            captureComponent(adminRestock, 1000, 640, "admin_restock.png");

            // 8. Admin Alerts
            System.out.println("Capturing Admin Alerts...");
            AlertsPanel adminAlerts = new AlertsPanel();
            captureComponent(adminAlerts, 1000, 640, "admin_alerts.png");


            // --- CASHIER SCENES ---
            System.out.println("Logging in as Cashier for Cashier POS screens...");
            authService.login("cashier1", "cashier123");

            // 9. Cashier POS (populated cart)
            System.out.println("Capturing Cashier POS with staged cart...");
            POSPanel pos = new POSPanel();
            populatePOSCart(pos);
            captureComponent(pos, 1100, 680, "cashier_pos.png");

            // 10. Cashier Receipt Dialog
            System.out.println("Capturing Cashier Receipt Dialog...");
            captureMockReceipt();

            // 11. Cashier Transaction History (first sale selected)
            System.out.println("Capturing Cashier Transaction History...");
            TransactionHistoryPanel cashierHistory = new TransactionHistoryPanel();
            selectHistoryRow(cashierHistory);
            captureComponent(cashierHistory, 1100, 680, "cashier_history.png");


            // --- MANAGER SCENES ---
            System.out.println("Logging in as Manager for Manager Dashboard screens...");
            authService.login("manager", "manager123");

            // 12. Manager Overview
            System.out.println("Capturing Manager Overview...");
            ManagerOverviewPanel managerOverview = new ManagerOverviewPanel();
            captureComponent(managerOverview, 1100, 680, "manager_overview.png");

            // 13. Manager Sales Report (loaded data)
            System.out.println("Capturing Manager Sales Report...");
            SalesReportPanel managerSales = new SalesReportPanel();
            triggerSalesReport(managerSales);
            captureComponent(managerSales, 1100, 680, "manager_sales_report.png");

            // 14. Manager Inventory Report (color coded)
            System.out.println("Capturing Manager Inventory Report...");
            InventoryReportPanel managerInventory = new InventoryReportPanel();
            triggerInventoryReport(managerInventory);
            captureComponent(managerInventory, 1100, 680, "manager_inventory.png");

            // 15. Manager EOD Report (loaded EOD content)
            System.out.println("Capturing Manager EOD Report...");
            EODReportPanel managerEOD = new EODReportPanel();
            triggerEODReport(managerEOD);
            captureComponent(managerEOD, 1100, 680, "manager_eod.png");

            // 16. Manager Alerts
            System.out.println("Capturing Manager Alerts...");
            LowStockAlertsPanel managerAlerts = new LowStockAlertsPanel();
            captureComponent(managerAlerts, 1100, 680, "manager_alerts.png");


            // --- ABOUT DIALOG ---
            // 17. About Dialog with Team Info
            System.out.println("Capturing About Dialog...");
            captureAboutDialog();

            System.out.println("\nAll screenshots generated successfully under docs/screenshots/!");

        } catch (Exception e) {
            System.err.println("Error generating screenshots: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection();
        }
    }

    private static void captureComponent(Component comp, int width, int height, String filename) {
        JFrame frame = null;
        if (!(comp instanceof JFrame)) {
            frame = new JFrame();
            frame.setSize(width, height);
            frame.setLayout(new BorderLayout());
            frame.add(comp, BorderLayout.CENTER);
            frame.setUndecorated(true);
            frame.pack();
            frame.setSize(width, height);
        } else {
            JFrame jf = (JFrame) comp;
            jf.setUndecorated(true);
            jf.setSize(width, height);
        }

        // Wait a short time for EDT rendering and SwingWorker threads to finish loading
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Render to BufferedImage
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        
        // Add rendering hints for crisp text
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        if (frame != null) {
            frame.paint(g2);
        } else {
            comp.paint(g2);
        }
        g2.dispose();

        // Save to docs/screenshots/
        File outFile = new File("docs/screenshots/" + filename);
        try {
            ImageIO.write(image, "png", outFile);
            System.out.println("-> Captured: " + filename);
        } catch (IOException e) {
            System.err.println("Failed to write screenshot " + filename + ": " + e.getMessage());
        }

        // Dispose temporary frame
        if (frame != null) {
            frame.dispose();
        } else if (comp instanceof JFrame) {
            ((JFrame) comp).dispose();
        }
    }

    private static void populatePOSCart(POSPanel pos) {
        try {
            // Wait for product list to load
            Thread.sleep(800);
            
            // Get private fields
            java.lang.reflect.Field currentSaleField = POSPanel.class.getDeclaredField("currentSale");
            currentSaleField.setAccessible(true);
            smartmart.model.Sale sale = (smartmart.model.Sale) currentSaleField.get(pos);
            
            java.lang.reflect.Field productListField = POSPanel.class.getDeclaredField("productList");
            productListField.setAccessible(true);
            List<Product> products = (List<Product>) productListField.get(pos);
            
            if (products != null && !products.isEmpty()) {
                SaleService saleService = new SaleService();
                // Add some items to the sale
                saleService.addItemToSale(sale, products.get(0), 5); // Coca Cola
                saleService.addItemToSale(sale, products.get(2), 2); // Lega Milk
                if (products.size() > 7) {
                    saleService.addItemToSale(sale, products.get(7), 3); // Nescafe (or whatever item is at 7)
                }
                
                // Invoke refreshCartTable and recalculateTotals
                java.lang.reflect.Method refreshMethod = POSPanel.class.getDeclaredMethod("refreshCartTable");
                refreshMethod.setAccessible(true);
                refreshMethod.invoke(pos);
                
                java.lang.reflect.Method recalcMethod = POSPanel.class.getDeclaredMethod("recalculateTotals");
                recalcMethod.setAccessible(true);
                recalcMethod.invoke(pos);
            }
        } catch (Exception ex) {
            System.err.println("Failed to populate POS Cart for screenshot: " + ex.getMessage());
        }
    }

    private static void selectHistoryRow(TransactionHistoryPanel history) {
        try {
            // Wait for sales list to load
            Thread.sleep(800);
            
            // Get private JTable
            java.lang.reflect.Field salesTableField = TransactionHistoryPanel.class.getDeclaredField("salesTable");
            salesTableField.setAccessible(true);
            JTable table = (JTable) salesTableField.get(history);
            if (table.getRowCount() > 0) {
                table.setRowSelectionInterval(0, 0); // Select first row
            }
        } catch (Exception ex) {
            System.err.println("Failed to select history row for screenshot: " + ex.getMessage());
        }
    }

    private static void triggerSalesReport(SalesReportPanel salesReportPanel) {
        try {
            Thread.sleep(500);
            java.lang.reflect.Method genMethod = SalesReportPanel.class.getDeclaredMethod("generateReport");
            genMethod.setAccessible(true);
            genMethod.invoke(salesReportPanel);
        } catch (Exception ex) {
            System.err.println("Failed to trigger sales report for screenshot: " + ex.getMessage());
        }
    }

    private static void triggerInventoryReport(InventoryReportPanel inventoryReportPanel) {
        try {
            Thread.sleep(500);
            java.lang.reflect.Method genMethod = InventoryReportPanel.class.getDeclaredMethod("generateReport");
            genMethod.setAccessible(true);
            genMethod.invoke(inventoryReportPanel);
        } catch (Exception ex) {
            System.err.println("Failed to trigger inventory report for screenshot: " + ex.getMessage());
        }
    }

    private static void triggerEODReport(EODReportPanel eodReportPanel) {
        try {
            Thread.sleep(500);
            java.lang.reflect.Method genMethod = EODReportPanel.class.getDeclaredMethod("generateReport");
            genMethod.setAccessible(true);
            genMethod.invoke(eodReportPanel);
        } catch (Exception ex) {
            System.err.println("Failed to trigger EOD report for screenshot: " + ex.getMessage());
        }
    }

    private static void captureMockReceipt() {
        try {
            SaleService saleService = new SaleService();
            Sale sale = saleService.createNewSale();
            Product p1 = new Product(1, "Coca Cola 500ml", null, null, 28.0, 115, 10);
            Product p2 = new Product(3, "Lega Milk 1L", null, null, 40.0, 43, 10);
            saleService.addItemToSale(sale, p1, 5);
            saleService.addItemToSale(sale, p2, 2);
            
            double subtotal = 5 * 28.0 + 2 * 40.0;
            double tax = subtotal * 0.15;
            double total = subtotal + tax;
            
            ReceiptDialog dialog = new ReceiptDialog(null, sale, subtotal, tax, total);
            captureComponent(dialog.getContentPane(), 420, 520, "cashier_receipt.png");
        } catch (Exception ex) {
            System.err.println("Failed to capture receipt screenshot: " + ex.getMessage());
        }
    }

    private static void captureAboutDialog() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("SmartMart System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIConstants.PRIMARY_COLOR);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel version = new JLabel("Version: " + UIConstants.APP_VERSION);
        version.setFont(UIConstants.FONT_SUBTITLE);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel("<html>SmartMart is an all-in-one Retail Store Management System<br>built for tracking products, employees, sales, and restocking.</html>");
        desc.setFont(UIConstants.FONT_BODY);
        desc.setBorder(BorderFactory.createEmptyBorder(15, 0, 20, 0));
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teamTitle = new JLabel("Development Team:");
        teamTitle.setFont(UIConstants.FONT_SUBTITLE.deriveFont(Font.BOLD));
        teamTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel team1 = new JLabel("- Melkamu Abyot (Lead Developer)");
        team1.setFont(UIConstants.FONT_BODY);
        team1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel team2 = new JLabel("- Solomon Haile (Database & Backend)");
        team2.setFont(UIConstants.FONT_BODY);
        team2.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel team3 = new JLabel("- Dawit Alemu (POS Specialist)");
        team3.setFont(UIConstants.FONT_BODY);
        team3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel team4 = new JLabel("- Yonas Girma (Cashier Operations)");
        team4.setFont(UIConstants.FONT_BODY);
        team4.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 3)));
        panel.add(version);
        panel.add(desc);
        panel.add(teamTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(team1);
        panel.add(team2);
        panel.add(team3);
        panel.add(team4);

        captureComponent(panel, 450, 360, "about_dialog.png");
    }
}
