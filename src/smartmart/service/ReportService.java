package smartmart.service;

import smartmart.dao.AlertDAO;
import smartmart.dao.EmployeeDAO;
import smartmart.dao.ProductDAO;
import smartmart.dao.SaleDAO;
import smartmart.exception.SmartMartException;
import smartmart.model.Alert;
import smartmart.model.Product;
import smartmart.model.Role;
import smartmart.model.Sale;
import smartmart.model.SaleItem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReportService {
    private final SaleDAO saleDAO;
    private final ProductDAO productDAO;
    private final EmployeeDAO employeeDAO;
    private final AlertDAO alertDAO;

    public ReportService() {
        this.saleDAO = new SaleDAO();
        this.productDAO = new ProductDAO();
        this.employeeDAO = new EmployeeDAO();
        this.alertDAO = new AlertDAO();
    }

    public abstract class Report {
        public abstract String generate() throws SmartMartException;
    }

    public class SalesReport extends Report {
        private final String date;

        public SalesReport(String date) {
            this.date = date;
        }

        @Override
        public String generate() throws SmartMartException {
            try {
                List<Sale> sales = saleDAO.getSalesByDate(date);
                StringBuilder sb = new StringBuilder();
                sb.append("=========================================\n");
                sb.append("             SALES REPORT                \n");
                sb.append("Date: ").append(date).append("\n");
                sb.append("=========================================\n");
                double totalRevenue = 0;
                if (sales.isEmpty()) {
                    sb.append("No sales transactions found for this date.\n");
                } else {
                    for (Sale s : sales) {
                        sb.append("Sale ID: ").append(s.getSaleId()).append("\n");
                        sb.append("Cashier: ").append(s.getCashier() != null ? s.getCashier().getFullName() : "N/A").append("\n");
                        sb.append("Time: ").append(s.getSaleDate()).append("\n");
                        sb.append("Items:\n");
                        for (SaleItem item : s.getItems()) {
                            sb.append("  - ").append(item.getProduct() != null ? item.getProduct().getProductName() : "Product")
                              .append(" x ").append(item.getQuantity())
                              .append(" @ ").append(String.format("%.2f", item.getUnitPrice()))
                              .append(" = ").append(String.format("%.2f", item.getSubtotal())).append("\n");
                        }
                        sb.append("Total: ").append(String.format("%.2f", s.getTotalAmount())).append("\n");
                        sb.append("-----------------------------------------\n");
                        totalRevenue += s.getTotalAmount();
                    }
                }
                sb.append("Total Revenue for Date: ").append(String.format("%.2f", totalRevenue)).append("\n");
                sb.append("=========================================\n");
                return sb.toString();
            } catch (SQLException e) {
                throw new SmartMartException("Failed to generate sales report: " + e.getMessage(), e);
            }
        }
    }

    public class InventoryReport extends Report {
        @Override
        public String generate() throws SmartMartException {
            try {
                List<Product> products = productDAO.getAllProducts();
                StringBuilder sb = new StringBuilder();
                sb.append("=====================================================================\n");
                sb.append("                          INVENTORY REPORT                           \n");
                sb.append("=====================================================================\n");
                sb.append(String.format("%-5s | %-25s | %-15s | %-10s | %-6s | %-8s\n", "ID", "Name", "Category", "Price", "Stock", "Status"));
                sb.append("---------------------------------------------------------------------\n");
                for (Product p : products) {
                    String status = p.isLowStock() ? "LOW STOCK" : "OK";
                    sb.append(String.format("%-5d | %-25s | %-15s | %-10.2f | %-6d | %-8s\n",
                            p.getProductId(),
                            p.getProductName(),
                            p.getCategory() != null ? p.getCategory().getCategoryName() : "N/A",
                            p.getPrice(),
                            p.getStockQty(),
                            status));
                }
                sb.append("=====================================================================\n");
                return sb.toString();
            } catch (SQLException e) {
                throw new SmartMartException("Failed to generate inventory report: " + e.getMessage(), e);
            }
        }
    }

    public class EODReport extends Report {
        @Override
        public String generate() throws SmartMartException {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String todayStr = sdf.format(new Date());

                double totalRevenue = saleDAO.getTotalRevenueToday();
                int totalTransactions = saleDAO.getTotalTransactionsToday();
                List<Object[]> topProducts = saleDAO.getTopSellingProducts(5);
                List<Alert> alerts = alertDAO.getUnresolvedAlerts();

                StringBuilder sb = new StringBuilder();
                sb.append("=========================================\n");
                sb.append("          END OF DAY (EOD) REPORT        \n");
                sb.append("Date: ").append(todayStr).append("\n");
                sb.append("=========================================\n");
                sb.append("Total Revenue Today:      ETB ").append(String.format("%.2f", totalRevenue)).append("\n");
                sb.append("Total Transactions Today: ").append(totalTransactions).append("\n");
                sb.append("-----------------------------------------\n");

                sb.append("Top 5 Selling Products:\n");
                if (topProducts.isEmpty()) {
                    sb.append("  No sales recorded today.\n");
                } else {
                    int rank = 1;
                    for (Object[] row : topProducts) {
                        sb.append("  ").append(rank++).append(". ").append(row[0]).append(" (Qty sold: ").append(row[1]).append(")\n");
                    }
                }

                sb.append("-----------------------------------------\n");
                sb.append("Active Alerts (Low Stock):\n");
                if (alerts.isEmpty()) {
                    sb.append("  No active alerts. Inventory levels healthy.\n");
                } else {
                    for (Alert a : alerts) {
                        sb.append("  - ").append(a.getMessage()).append("\n");
                    }
                }
                sb.append("=========================================\n");
                return sb.toString();
            } catch (SQLException e) {
                throw new SmartMartException("Failed to generate EOD report: " + e.getMessage(), e);
            }
        }
    }

    public String generateSalesReport(String date) throws SmartMartException {
        AuthService.requireRole(Role.MANAGER, Role.ADMIN);
        return new SalesReport(date).generate();
    }

    public String generateInventoryReport() throws SmartMartException {
        AuthService.requireRole(Role.MANAGER, Role.ADMIN);
        return new InventoryReport().generate();
    }

    public String generateEODReport() throws SmartMartException {
        AuthService.requireRole(Role.MANAGER, Role.ADMIN);
        return new EODReport().generate();
    }

    public boolean saveReportToFile(String reportContent, String fileName) {
        File docsDir = new File("docs");
        if (!docsDir.exists()) {
            docsDir.mkdirs();
        }
        File reportFile = new File(docsDir, fileName);
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(reportContent);
            return true;
        } catch (IOException e) {
            System.err.println("IOException saving report to file: " + e.getMessage());
            return false;
        }
    }
}
