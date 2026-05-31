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
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                Date now = new Date();
                String todayStr = sdfDate.format(now);
                String timeStr = sdfTime.format(now);

                String managerName = AuthService.getCurrentUser() != null ? AuthService.getCurrentUser().getFullName() : "System";

                List<Sale> todaySales = saleDAO.getSalesByDate(todayStr);
                int totalTransactions = todaySales.size();
                double totalRevenue = 0;
                double highestSaleVal = 0;
                String highestSaleRec = "N/A";
                
                java.util.Map<String, double[]> cashierStats = new java.util.HashMap<>(); // [count, revenue]

                for (Sale s : todaySales) {
                    totalRevenue += s.getTotalAmount();
                    if (s.getTotalAmount() > highestSaleVal) {
                        highestSaleVal = s.getTotalAmount();
                        highestSaleRec = String.format("REC-%03d", s.getSaleId());
                    }
                    String cashierName = s.getCashier() != null ? s.getCashier().getFullName() : "Unknown";
                    double[] stats = cashierStats.computeIfAbsent(cashierName, k -> new double[2]);
                    stats[0]++;
                    stats[1] += s.getTotalAmount();
                }
                
                double avgSale = totalTransactions > 0 ? totalRevenue / totalTransactions : 0;

                List<Object[]> topProducts = saleDAO.getTopSellingProducts(5);
                
                List<Product> allProducts = productDAO.getAllProducts();
                int lowStockCount = 0;
                int outOfStockCount = 0;
                List<Product> lowStockList = new java.util.ArrayList<>();
                for (Product p : allProducts) {
                    if (p.getStockQty() == 0) {
                        outOfStockCount++;
                    } else if (p.getStockQty() <= p.getLowStockLimit()) {
                        lowStockCount++;
                        lowStockList.add(p);
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append("================================================\n");
                sb.append("SMARTMART RETAIL STORE\n");
                sb.append("END OF DAY REPORT\n");
                sb.append(String.format("Date         : %s\n", todayStr));
                sb.append(String.format("Generated by : %s\n", managerName));
                sb.append(String.format("Generated at : %s\n", timeStr));
                sb.append("\nSALES SUMMARY\n");
                sb.append(String.format("Total Transactions  : %d\n", totalTransactions));
                sb.append(String.format("Total Revenue       : ETB %,.2f\n", totalRevenue));
                sb.append(String.format("Average Sale Value  : ETB %,.2f\n", avgSale));
                sb.append(String.format("Highest Sale        : ETB %,.2f (%s)\n", highestSaleVal, highestSaleRec));
                
                sb.append("\nTOP 5 SELLING PRODUCTS\n\n");
                if (topProducts.isEmpty()) {
                    sb.append("  No products sold today.\n");
                } else {
                    for (Object[] row : topProducts) {
                        String pName = (String) row[0];
                        int qty = (Integer) row[1];
                        // Calculate approximate revenue for the top product by checking todaySales (simplification for report)
                        double prodRev = 0;
                        for (Sale s : todaySales) {
                            for (SaleItem item : s.getItems()) {
                                if (item.getProduct() != null && item.getProduct().getProductName().equals(pName)) {
                                    prodRev += item.getSubtotal();
                                }
                            }
                        }
                        sb.append(String.format("%-22s %2d units   ETB %,.2f\n", pName, qty, prodRev));
                    }
                }

                sb.append("\nINVENTORY ALERTS\n");
                sb.append(String.format("Low Stock Items     : %d\n", lowStockCount));
                sb.append(String.format("Out of Stock Items  : %d\n", outOfStockCount));
                for (Product p : lowStockList) {
                    sb.append(String.format("- %s (Qty: %d, Limit: %d)\n", p.getProductName(), p.getStockQty(), p.getLowStockLimit()));
                }

                sb.append("\nCASHIER PERFORMANCE\n");
                if (cashierStats.isEmpty()) {
                    sb.append("  No cashier activity recorded.\n");
                } else {
                    for (java.util.Map.Entry<String, double[]> entry : cashierStats.entrySet()) {
                        sb.append(String.format("- %-15s : %3d transactions, ETB %,.2f\n", 
                                entry.getKey(), (int)entry.getValue()[0], entry.getValue()[1]));
                    }
                }
                
                sb.append("================================================\n");
                sb.append("END OF REPORT\n");
                
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
