package smartmart.dao;

import smartmart.model.Admin;
import smartmart.model.Cashier;
import smartmart.model.Category;
import smartmart.model.Manager;
import smartmart.model.Product;
import smartmart.model.Role;
import smartmart.model.Sale;
import smartmart.model.SaleItem;
import smartmart.model.Supplier;
import smartmart.model.User;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SaleDAO {

    public boolean processSale(Sale sale) throws SQLException {
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement psSale = null;
        PreparedStatement psItem = null;
        ResultSet rsKeys = null;
        boolean originalAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);

            String sqlSale = "INSERT INTO sales (cashier_id, total_amount, sale_date) VALUES (?, ?, ?)";
            psSale = conn.prepareStatement(sqlSale, PreparedStatement.RETURN_GENERATED_KEYS);
            psSale.setInt(1, sale.getCashier() != null ? sale.getCashier().getUserId() : 0);
            psSale.setDouble(2, sale.getTotalAmount());
            // Fall back to the current timestamp (a real value, not the literal
            // text "datetime('now')") when no sale date was set.
            String saleDate = sale.getSaleDate();
            if (saleDate == null) {
                saleDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            }
            psSale.setString(3, saleDate);

            int affected = psSale.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating sale failed, no rows affected.");
            }

            rsKeys = psSale.getGeneratedKeys();
            int saleId = 0;
            if (rsKeys.next()) {
                saleId = rsKeys.getInt(1);
            } else {
                throw new SQLException("Creating sale failed, no ID obtained.");
            }
            sale.setSaleId(saleId);

            String sqlItem = "INSERT INTO sale_items (sale_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            psItem = conn.prepareStatement(sqlItem);
            for (SaleItem item : sale.getItems()) {
                psItem.setInt(1, saleId);
                psItem.setInt(2, item.getProduct() != null ? item.getProduct().getProductId() : 0);
                psItem.setInt(3, item.getQuantity());
                psItem.setDouble(4, item.getUnitPrice());
                psItem.setDouble(5, item.getSubtotal());
                psItem.addBatch();
            }
            psItem.executeBatch();

            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            if (rsKeys != null) {
                rsKeys.close();
            }
            if (psSale != null) {
                psSale.close();
            }
            if (psItem != null) {
                psItem.close();
            }
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    public List<Sale> getAllSales() throws SQLException {
        List<Sale> list = new ArrayList<>();
        String query = "SELECT s.*, u.username, u.password, u.role, u.full_name AS cashier_fullname, u.is_active " +
                       "FROM sales s " +
                       "JOIN users u ON s.cashier_id = u.user_id";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                Sale sale = mapSale(rs, conn);
                list.add(sale);
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

    public List<Sale> getSalesByDate(String date) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String query = "SELECT s.*, u.username, u.password, u.role, u.full_name AS cashier_fullname, u.is_active " +
                       "FROM sales s " +
                       "JOIN users u ON s.cashier_id = u.user_id " +
                       "WHERE date(s.sale_date) = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, date);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSale(rs, conn));
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

    public List<Sale> getSalesByDateRange(String startDate, String endDate) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String query = "SELECT s.*, u.username, u.password, u.role, u.full_name AS cashier_fullname, u.is_active " +
                       "FROM sales s " +
                       "JOIN users u ON s.cashier_id = u.user_id " +
                       "WHERE date(s.sale_date) BETWEEN ? AND ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSale(rs, conn));
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

    public List<Sale> getSalesByCashier(int cashierId) throws SQLException {
        List<Sale> list = new ArrayList<>();
        String query = "SELECT s.*, u.username, u.password, u.role, u.full_name AS cashier_fullname, u.is_active " +
                       "FROM sales s " +
                       "JOIN users u ON s.cashier_id = u.user_id " +
                       "WHERE s.cashier_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, cashierId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapSale(rs, conn));
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

    public double getTotalRevenueToday() throws SQLException {
        String query = "SELECT SUM(total_amount) FROM sales WHERE date(sale_date) = date('now')";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    public double getTotalRevenueByDateRange(String startDate, String endDate) throws SQLException {
        String query = "SELECT SUM(total_amount) FROM sales WHERE date(sale_date) BETWEEN ? AND ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, startDate);
            ps.setString(2, endDate);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0.0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<Object[]> getTopSellingProducts(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String query = "SELECT p.product_name, SUM(si.quantity) AS total_sold " +
                       "FROM sale_items si " +
                       "JOIN products p ON si.product_id = p.product_id " +
                       "GROUP BY si.product_id " +
                       "ORDER BY total_sold DESC " +
                       "LIMIT ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, limit);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getString("product_name"),
                        rs.getInt("total_sold")
                });
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

    public int getTotalTransactionsToday() throws SQLException {
        String query = "SELECT COUNT(*) FROM sales WHERE date(sale_date) = date('now')";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    private Sale mapSale(ResultSet rs, Connection conn) throws SQLException {
        int userId = rs.getInt("cashier_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String roleStr = rs.getString("role");
        String fullName = rs.getString("cashier_fullname");
        boolean isActive = rs.getInt("is_active") == 1;

        Role role = Role.valueOf(roleStr);
        User cashier = null;
        switch (role) {
            case ADMIN:
                cashier = new Admin(userId, username, password, role, fullName, isActive);
                break;
            case MANAGER:
                cashier = new Manager(userId, username, password, role, fullName, isActive);
                break;
            case CASHIER:
                cashier = new Cashier(userId, username, password, role, fullName, isActive);
                break;
        }

        int saleId = rs.getInt("sale_id");
        List<SaleItem> items = getSaleItemsForSale(saleId, conn);

        return new Sale(
                saleId,
                cashier,
                items,
                rs.getDouble("total_amount"),
                rs.getString("sale_date")
        );
    }

    private List<SaleItem> getSaleItemsForSale(int saleId, Connection conn) throws SQLException {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT si.*, p.product_name, p.price, p.stock_qty, p.low_stock_limit, p.category_id, p.supplier_id, " +
                     "       c.category_name, sup.name AS supplier_name, sup.contact_phone AS supplier_phone, sup.email AS supplier_email, sup.address AS supplier_address " +
                     "FROM sale_items si " +
                     "JOIN products p ON si.product_id = p.product_id " +
                     "JOIN categories c ON p.category_id = c.category_id " +
                     "JOIN suppliers sup ON p.supplier_id = sup.supplier_id " +
                     "WHERE si.sale_id = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, saleId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category(rs.getInt("category_id"), rs.getString("category_name"));
                Supplier sup = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("supplier_name"),
                        rs.getString("supplier_phone"),
                        rs.getString("supplier_email"),
                        rs.getString("supplier_address")
                );
                Product prod = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        cat,
                        sup,
                        rs.getDouble("price"),
                        rs.getInt("stock_qty"),
                        rs.getInt("low_stock_limit")
                );
                items.add(new SaleItem(
                        rs.getInt("item_id"),
                        prod,
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price")
                ));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return items;
    }
}
