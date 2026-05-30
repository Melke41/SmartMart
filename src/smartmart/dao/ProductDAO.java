package smartmart.dao;

import smartmart.exception.ProductDeletionException;
import smartmart.model.Category;
import smartmart.model.Product;
import smartmart.model.Supplier;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String query = "SELECT p.*, c.category_name, s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address " +
                       "FROM products p " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers s ON p.supplier_id = s.supplier_id";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    public Product getProductById(int productId) throws SQLException {
        String query = "SELECT p.*, c.category_name, s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address " +
                       "FROM products p " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                       "WHERE p.product_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, productId);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapProduct(rs);
            }
            return null;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<Product> searchProducts(String query) throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name, s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                     "WHERE p.product_name LIKE ? OR c.category_name LIKE ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            String pattern = "%" + query + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    public List<Product> getLowStockProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String query = "SELECT p.*, c.category_name, s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address " +
                       "FROM products p " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                       "WHERE p.stock_qty <= p.low_stock_limit";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProduct(rs));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return list;
    }

    public boolean addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (product_name, category_id, supplier_id, price, stock_qty, low_stock_limit) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategory() != null ? product.getCategory().getCategoryId() : 0);
            ps.setInt(3, product.getSupplier() != null ? product.getSupplier().getSupplierId() : 0);
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setInt(6, product.getLowStockLimit());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET product_name = ?, category_id = ?, supplier_id = ?, price = ?, stock_qty = ?, low_stock_limit = ? WHERE product_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, product.getProductName());
            ps.setInt(2, product.getCategory() != null ? product.getCategory().getCategoryId() : 0);
            ps.setInt(3, product.getSupplier() != null ? product.getSupplier().getSupplierId() : 0);
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getStockQty());
            ps.setInt(6, product.getLowStockLimit());
            ps.setInt(7, product.getProductId());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean updateStock(int productId, int newQty) throws SQLException {
        String query = "UPDATE products SET stock_qty = ? WHERE product_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, newQty);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean deleteProduct(int productId) throws SQLException, ProductDeletionException {
        // Check for sales history
        String checkSql = "SELECT COUNT(*) FROM sale_items WHERE product_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement psCheck = null;
        ResultSet rsCheck = null;
        try {
            psCheck = conn.prepareStatement(checkSql);
            psCheck.setInt(1, productId);
            rsCheck = psCheck.executeQuery();
            if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                throw new ProductDeletionException("Product has sales history and cannot be deleted.");
            }
        } finally {
            if (rsCheck != null) {
                rsCheck.close();
            }
            if (psCheck != null) {
                psCheck.close();
            }
        }

        // Delete if no sales history
        String deleteSql = "DELETE FROM products WHERE product_id = ?";
        PreparedStatement psDelete = null;
        try {
            psDelete = conn.prepareStatement(deleteSql);
            psDelete.setInt(1, productId);
            int rows = psDelete.executeUpdate();
            return rows > 0;
        } finally {
            if (psDelete != null) {
                psDelete.close();
            }
        }
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Category category = new Category(
                rs.getInt("category_id"),
                rs.getString("category_name")
        );
        Supplier supplier = new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("supplier_phone"),
                rs.getString("supplier_email"),
                rs.getString("supplier_address")
        );
        return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                category,
                supplier,
                rs.getDouble("price"),
                rs.getInt("stock_qty"),
                rs.getInt("low_stock_limit")
        );
    }
}
