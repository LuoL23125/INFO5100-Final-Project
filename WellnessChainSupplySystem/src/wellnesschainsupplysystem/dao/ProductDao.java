package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.Product;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    public void create(Product product) {
        String sql = "INSERT INTO product (name, category, unit, unit_price) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setString(3, product.getUnit());
            stmt.setBigDecimal(4, product.getUnitPrice());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows inserted for product");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting product");
            e.printStackTrace();
        }
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, category, unit, unit_price FROM product";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategory(rs.getString("category"));
                p.setUnit(rs.getString("unit"));
                p.setUnitPrice(rs.getBigDecimal("unit_price"));

                products.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching products");
            e.printStackTrace();
        }

        return products;
    }

    public void update(Product product) {
        String sql = "UPDATE product SET name = ?, category = ?, unit = ?, unit_price = ? WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory());
            stmt.setString(3, product.getUnit());
            stmt.setBigDecimal(4, product.getUnitPrice());
            stmt.setInt(5, product.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows updated for product id=" + product.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating product");
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM product WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows deleted for product id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting product");
            e.printStackTrace();
        }
    }
}
