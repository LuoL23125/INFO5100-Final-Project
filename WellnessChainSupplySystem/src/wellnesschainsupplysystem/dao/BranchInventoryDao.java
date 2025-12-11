package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.BranchInventory;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchInventoryDao {

    public List<BranchInventory> findByBranchId(int branchId) {
        List<BranchInventory> list = new ArrayList<>();
        String sql = "SELECT id, branch_id, product_id, quantity_on_hand, reorder_threshold " +
                     "FROM branch_inventory WHERE branch_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BranchInventory inv = new BranchInventory();
                    inv.setId(rs.getInt("id"));
                    inv.setBranchId(rs.getInt("branch_id"));
                    inv.setProductId(rs.getInt("product_id"));
                    inv.setQuantityOnHand(rs.getInt("quantity_on_hand"));
                    inv.setReorderThreshold(rs.getInt("reorder_threshold"));
                    list.add(inv);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching branch_inventory for branchId=" + branchId);
            e.printStackTrace();
        }

        return list;
    }

    public void create(BranchInventory inv) {
        String sql = "INSERT INTO branch_inventory " +
                     "(branch_id, product_id, quantity_on_hand, reorder_threshold) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, inv.getBranchId());
            stmt.setInt(2, inv.getProductId());
            stmt.setInt(3, inv.getQuantityOnHand());
            stmt.setInt(4, inv.getReorderThreshold());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows inserted for branch_inventory");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    inv.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting branch_inventory");
            e.printStackTrace();
        }
    }

    public void update(BranchInventory inv) {
        String sql = "UPDATE branch_inventory " +
                     "SET quantity_on_hand = ?, reorder_threshold = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inv.getQuantityOnHand());
            stmt.setInt(2, inv.getReorderThreshold());
            stmt.setInt(3, inv.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows updated for branch_inventory id=" + inv.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating branch_inventory");
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM branch_inventory WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows deleted for branch_inventory id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting branch_inventory");
            e.printStackTrace();
        }
    }
}
