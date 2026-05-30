package smartmart.dao;

import smartmart.model.Category;
import smartmart.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    public List<Category> getAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String query = "SELECT * FROM categories";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
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
        return list;
    }

    public boolean addCategory(Category category) throws SQLException {
        String query = "INSERT INTO categories (category_name) VALUES (?)";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setString(1, category.getCategoryName());
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public boolean deleteCategory(int categoryId) throws SQLException {
        String query = "DELETE FROM categories WHERE category_id = ?";
        Connection conn = DatabaseConnection.getInstance();
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(query);
            ps.setInt(1, categoryId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
}
