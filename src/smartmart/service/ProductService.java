package smartmart.service;

import smartmart.dao.AlertDAO;
import smartmart.dao.ProductDAO;
import smartmart.exception.InvalidProductException;
import smartmart.exception.ProductHasSalesHistoryException;
import smartmart.exception.SmartMartException;
import smartmart.model.Product;
import smartmart.model.Role;

import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;
    private final AlertDAO alertDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
        this.alertDAO = new AlertDAO();
    }

    public List<Product> getAllProducts() throws SmartMartException {
        try {
            return productDAO.getAllProducts();
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving all products: " + e.getMessage(), e);
        }
    }

    public List<Product> searchProducts(String query) throws SmartMartException {
        try {
            return productDAO.searchProducts(query);
        } catch (SQLException e) {
            throw new SmartMartException("Database error searching products: " + e.getMessage(), e);
        }
    }

    public List<Product> getLowStockProducts() throws SmartMartException {
        try {
            return productDAO.getLowStockProducts();
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving low stock products: " + e.getMessage(), e);
        }
    }

    public void addProduct(Product product) throws SmartMartException, InvalidProductException {
        AuthService.requireRole(Role.ADMIN);
        validateProduct(product);
        try {
            productDAO.addProduct(product);
        } catch (SQLException e) {
            throw new SmartMartException("Database error adding product: " + e.getMessage(), e);
        }
    }

    public void updateProduct(Product product) throws SmartMartException, InvalidProductException {
        AuthService.requireRole(Role.ADMIN);
        validateProduct(product);
        try {
            productDAO.updateProduct(product);
        } catch (SQLException e) {
            throw new SmartMartException("Database error updating product: " + e.getMessage(), e);
        }
    }

    public void deleteProduct(int productId) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        String productName = "Unknown Product";
        try {
            Product prod = productDAO.getProductById(productId);
            if (prod != null) {
                productName = prod.getProductName();
            }
            productDAO.deleteProduct(productId);
        } catch (smartmart.exception.ProductDeletionException e) {
            throw new ProductHasSalesHistoryException(productName);
        } catch (SQLException e) {
            throw new SmartMartException("Database error deleting product: " + e.getMessage(), e);
        }
    }

    public void checkLowStockAlerts() throws SmartMartException {
        try {
            alertDAO.checkAndCreateLowStockAlerts();
        } catch (SQLException e) {
            throw new SmartMartException("Database error checking low stock alerts: " + e.getMessage(), e);
        }
    }

    private void validateProduct(Product product) throws InvalidProductException {
        if (product == null) {
            throw new InvalidProductException("Product cannot be null.");
        }
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new InvalidProductException("Product name cannot be empty.");
        }
        if (product.getPrice() <= 0) {
            throw new InvalidProductException("Price must be greater than 0.");
        }
        if (product.getStockQty() < 0) {
            throw new InvalidProductException("Stock quantity cannot be negative.");
        }
    }
}
