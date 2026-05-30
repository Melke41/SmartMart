package smartmart.dao;

import smartmart.model.Category;
import smartmart.model.Product;
import smartmart.model.RestockOrder;
import smartmart.model.Supplier;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestockOrderDAO {

    public boolean createRestockOrder(RestockOrder order) throws SQLException {
        String query = "INSERT INTO restock_orders (product_id, supplier_id, quantity, status, order_date, received_date) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, order.getProduct() != null ? order.getProduct().getProductId() : 0);
            ps.setInt(2, order.getSupplier() != null ? order.getSupplier().getSupplierId() : 0);
            ps.setInt(3, order.getQuantity());
            ps.setString(4, order.getStatus() != null ? order.getStatus() : "PENDING");
            ps.setString(5, order.getOrderDate() != null ? order.getOrderDate() : "datetime('now')");
            ps.setString(6, order.getReceivedDate());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<RestockOrder> getAllOrders() throws SQLException {
        List<RestockOrder> list = new ArrayList<>();
        String query = "SELECT ro.*, " +
                       "p.product_name, p.price, p.stock_qty, p.low_stock_limit, p.category_id, p.supplier_id AS prod_sup_id, " +
                       "c.category_name, " +
                       "s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address, " +
                       "ps.name AS prod_sup_name, ps.contact_phone AS prod_sup_phone, ps.email AS prod_sup_email, ps.address AS prod_sup_address " +
                       "FROM restock_orders ro " +
                       "JOIN products p ON ro.product_id = p.product_id " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers ps ON p.supplier_id = ps.supplier_id " +
                       "JOIN suppliers s ON ro.supplier_id = s.supplier_id";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRestockOrder(rs));
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

    public List<RestockOrder> getPendingOrders() throws SQLException {
        List<RestockOrder> list = new ArrayList<>();
        String query = "SELECT ro.*, " +
                       "p.product_name, p.price, p.stock_qty, p.low_stock_limit, p.category_id, p.supplier_id AS prod_sup_id, " +
                       "c.category_name, " +
                       "s.name AS supplier_name, s.contact_phone AS supplier_phone, s.email AS supplier_email, s.address AS supplier_address, " +
                       "ps.name AS prod_sup_name, ps.contact_phone AS prod_sup_phone, ps.email AS prod_sup_email, ps.address AS prod_sup_address " +
                       "FROM restock_orders ro " +
                       "JOIN products p ON ro.product_id = p.product_id " +
                       "JOIN categories c ON p.category_id = c.category_id " +
                       "JOIN suppliers ps ON p.supplier_id = ps.supplier_id " +
                       "JOIN suppliers s ON ro.supplier_id = s.supplier_id " +
                       "WHERE ro.status = 'PENDING'";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRestockOrder(rs));
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

    public boolean markAsReceived(int orderId) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement psGet = null;
        PreparedStatement psOrder = null;
        PreparedStatement psProd = null;
        ResultSet rs = null;
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            // 1. Get quantity, product, and current status
            String sqlGet = "SELECT product_id, quantity, status FROM restock_orders WHERE order_id = ?";
            psGet = conn.prepareStatement(sqlGet);
            psGet.setInt(1, orderId);
            rs = psGet.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Restock order not found: ID " + orderId);
            }
            int productId = rs.getInt("product_id");
            int quantity = rs.getInt("quantity");
            String status = rs.getString("status");

            if ("RECEIVED".equalsIgnoreCase(status)) {
                throw new SQLException("Restock order is already RECEIVED.");
            }

            // 2. Update status and received_date
            String sqlOrder = "UPDATE restock_orders SET status = 'RECEIVED', received_date = datetime('now') WHERE order_id = ?";
            psOrder = conn.prepareStatement(sqlOrder);
            psOrder.setInt(1, orderId);
            int affected = psOrder.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Failed to update restock order status.");
            }

            // 3. Update stock_qty in products
            String sqlProd = "UPDATE products SET stock_qty = stock_qty + ? WHERE product_id = ?";
            psProd = conn.prepareStatement(sqlProd);
            psProd.setInt(1, quantity);
            psProd.setInt(2, productId);
            affected = psProd.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Failed to update product stock.");
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (psGet != null) {
                psGet.close();
            }
            if (psOrder != null) {
                psOrder.close();
            }
            if (psProd != null) {
                psProd.close();
            }
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public boolean cancelOrder(int orderId) throws SQLException {
        String query = "UPDATE restock_orders SET status = 'CANCELLED' WHERE order_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, orderId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    private RestockOrder mapRestockOrder(ResultSet rs) throws SQLException {
        Category cat = new Category(
                rs.getInt("category_id"),
                rs.getString("category_name")
        );
        Supplier prodSup = new Supplier(
                rs.getInt("prod_sup_id"),
                rs.getString("prod_sup_name"),
                rs.getString("prod_sup_phone"),
                rs.getString("prod_sup_email"),
                rs.getString("prod_sup_address")
        );
        Product prod = new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                cat,
                prodSup,
                rs.getDouble("price"),
                rs.getInt("stock_qty"),
                rs.getInt("low_stock_limit")
        );
        Supplier supplier = new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("supplier_name"),
                rs.getString("supplier_phone"),
                rs.getString("supplier_email"),
                rs.getString("supplier_address")
        );

        return new RestockOrder(
                rs.getInt("order_id"),
                prod,
                supplier,
                rs.getInt("quantity"),
                rs.getString("status"),
                rs.getString("order_date"),
                rs.getString("received_date")
        );
    }
}
