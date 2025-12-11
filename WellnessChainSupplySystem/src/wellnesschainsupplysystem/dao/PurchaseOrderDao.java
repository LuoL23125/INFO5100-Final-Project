package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.PurchaseOrder;
import wellnesschainsupplysystem.model.PurchaseOrderStatus;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDao {

    // Create PO with a single product item
    public int createWithSingleItem(int branchId, int createdByUserId,
                                    int productId, int quantity, BigDecimal unitPrice) {
        String poSql = "INSERT INTO purchase_order " +
                "(branch_id, status, created_at, submitted_at, created_by_user_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        String itemSql = "INSERT INTO purchase_order_item " +
                "(purchase_order_id, product_id, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection()) {
            conn.setAutoCommit(false);

            int poId;

            try (PreparedStatement poStmt = conn.prepareStatement(poSql, Statement.RETURN_GENERATED_KEYS)) {
                poStmt.setInt(1, branchId);
                poStmt.setString(2, PurchaseOrderStatus.SUBMITTED.name());
                poStmt.setTimestamp(3, Timestamp.valueOf(now));
                poStmt.setTimestamp(4, Timestamp.valueOf(now));
                poStmt.setInt(5, createdByUserId);

                poStmt.executeUpdate();

                try (ResultSet rs = poStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        poId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        throw new SQLException("Failed to get generated purchase_order id");
                    }
                }
            }

            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                itemStmt.setInt(1, poId);
                itemStmt.setInt(2, productId);
                itemStmt.setInt(3, quantity);
                itemStmt.setBigDecimal(4, unitPrice);
                itemStmt.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);
            return poId;

        } catch (SQLException e) {
            System.err.println("Error creating purchase order with item");
            e.printStackTrace();
            return -1;
        }
    }

    // For Branch Admin: list POs by branch with summary fields
    public List<PurchaseOrder> findByBranch(int branchId) {
        List<PurchaseOrder> list = new ArrayList<>();

        String sql = "SELECT po.id, po.branch_id, po.status, po.created_at, " +
                "b.name AS branch_name, p.name AS product_name, poi.quantity " +
                "FROM purchase_order po " +
                "JOIN clinic_branch b ON po.branch_id = b.id " +
                "JOIN purchase_order_item poi ON poi.purchase_order_id = po.id " +
                "JOIN product p ON p.id = poi.product_id " +
                "WHERE po.branch_id = ? " +
                "ORDER BY po.created_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setId(rs.getInt("id"));
                    po.setBranchId(rs.getInt("branch_id"));
                    po.setStatus(PurchaseOrderStatus.valueOf(rs.getString("status")));
                    Timestamp createdTs = rs.getTimestamp("created_at");
                    if (createdTs != null) {
                        po.setCreatedAt(createdTs.toLocalDateTime());
                    }
                    po.setBranchName(rs.getString("branch_name"));
                    po.setProductName(rs.getString("product_name"));
                    po.setQuantity(rs.getInt("quantity"));
                    list.add(po);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching purchase orders for branchId=" + branchId);
            e.printStackTrace();
        }

        return list;
    }

    // For Supplier Admin: list all POs
    public List<PurchaseOrder> findAllWithSummary() {
        List<PurchaseOrder> list = new ArrayList<>();

        String sql = "SELECT po.id, po.branch_id, po.status, po.created_at, " +
                "b.name AS branch_name, p.name AS product_name, poi.quantity " +
                "FROM purchase_order po " +
                "JOIN clinic_branch b ON po.branch_id = b.id " +
                "JOIN purchase_order_item poi ON poi.purchase_order_id = po.id " +
                "JOIN product p ON p.id = poi.product_id " +
                "ORDER BY po.created_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder();
                po.setId(rs.getInt("id"));
                po.setBranchId(rs.getInt("branch_id"));
                po.setStatus(PurchaseOrderStatus.valueOf(rs.getString("status")));
                Timestamp createdTs = rs.getTimestamp("created_at");
                if (createdTs != null) {
                    po.setCreatedAt(createdTs.toLocalDateTime());
                }
                po.setBranchName(rs.getString("branch_name"));
                po.setProductName(rs.getString("product_name"));
                po.setQuantity(rs.getInt("quantity"));
                list.add(po);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all purchase orders");
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int poId, PurchaseOrderStatus newStatus, String supplierComment) {
        String sql = "UPDATE purchase_order " +
                "SET status = ?, reviewed_at = ?, supplier_comment = ? " +
                "WHERE id = ?";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, supplierComment);
            stmt.setInt(4, poId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating purchase order status");
            e.printStackTrace();
        }
    }
}
