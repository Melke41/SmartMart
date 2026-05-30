package smartmart.service;

import smartmart.dao.ProductDAO;
import smartmart.dao.SaleDAO;
import smartmart.exception.InsufficientStockException;
import smartmart.exception.OutOfStockException;
import smartmart.exception.SmartMartException;
import smartmart.model.Product;
import smartmart.model.Role;
import smartmart.model.Sale;
import smartmart.model.SaleItem;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SaleService {
    private final SaleDAO saleDAO;
    private final ProductDAO productDAO;
    private final ProductService productService;

    public SaleService() {
        this.saleDAO = new SaleDAO();
        this.productDAO = new ProductDAO();
        this.productService = new ProductService();
    }

    public Sale createNewSale() {
        Sale sale = new Sale();
        sale.setCashier(AuthService.getCurrentUser());
        sale.setItems(new ArrayList<>());
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sale.setSaleDate(sdf.format(new Date()));
        return sale;
    }

    public void addItemToSale(Sale sale, Product product, int quantity) throws InsufficientStockException, OutOfStockException {
        if (product.getStockQty() <= 0) {
            throw new OutOfStockException(product.getProductName(), quantity);
        }
        if (product.getStockQty() < quantity) {
            throw new InsufficientStockException(product.getProductName(), quantity, product.getStockQty());
        }
        SaleItem item = new SaleItem(0, product, quantity, product.getPrice());
        sale.addItem(item);
    }

    public void removeItemFromSale(Sale sale, SaleItem item) {
        sale.removeItem(item);
    }

    public Sale processSale(Sale sale) throws SmartMartException {
        AuthService.requireRole(Role.CASHIER, Role.ADMIN);
        
        if (sale == null || sale.getItems() == null || sale.getItems().isEmpty()) {
            throw new SmartMartException("Sale must have at least one item.");
        }

        try {
            // 1. Process sale transaction (inserts sale and items)
            saleDAO.processSale(sale);

            // 2. Reduce stock qty for each item
            for (SaleItem item : sale.getItems()) {
                Product p = item.getProduct();
                if (p != null) {
                    int newQty = p.getStockQty() - item.getQuantity();
                    productDAO.updateStock(p.getProductId(), newQty);
                    p.setStockQty(newQty); // update in-memory stock
                }
            }

            // 3. Trigger low stock checks and alerts
            productService.checkLowStockAlerts();

            return sale;
        } catch (SQLException e) {
            throw new SmartMartException("Database error processing sale: " + e.getMessage(), e);
        }
    }

    public List<Sale> getSalesByDate(String date) throws SmartMartException {
        try {
            return saleDAO.getSalesByDate(date);
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving sales by date: " + e.getMessage(), e);
        }
    }

    public double getTotalRevenueToday() throws SmartMartException {
        try {
            return saleDAO.getTotalRevenueToday();
        } catch (SQLException e) {
            throw new SmartMartException("Database error calculating today's revenue: " + e.getMessage(), e);
        }
    }

    public List<Object[]> getTopSellingProducts(int limit) throws SmartMartException {
        try {
            return saleDAO.getTopSellingProducts(limit);
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving top selling products: " + e.getMessage(), e);
        }
    }

    public int getTotalTransactionsToday() throws SmartMartException {
        try {
            return saleDAO.getTotalTransactionsToday();
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving transaction count: " + e.getMessage(), e);
        }
    }
}
