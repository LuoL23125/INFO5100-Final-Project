package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.WorkRequest;
import wellnesschainsupplysystem.model.WorkRequestStatus;
import wellnesschainsupplysystem.model.WorkRequestType;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;   // ADD THIS
import java.util.List;        // ADD THIS

public class WorkRequestDao {

    public void createForPurchaseOrder(int poId, int createdByUserId, String comments) {
        String sql = "INSERT INTO work_request "
                + "(type, status, created_at, updated_at, created_by_user_id, related_po_id, comments) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, WorkRequestType.PURCHASE_ORDER.name());
            stmt.setString(2, WorkRequestStatus.OPEN.name());
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setInt(5, createdByUserId);
            stmt.setInt(6, poId);
            stmt.setString(7, comments);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating work_request for purchase order");
            e.printStackTrace();
        }
    }

    public void updateStatusForPurchaseOrder(int poId, WorkRequestStatus newStatus, String comment) {
        String sql = "UPDATE work_request "
                + "SET status = ?, updated_at = ?, comments = ? "
                + "WHERE related_po_id = ? AND type = ?";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, comment);
            stmt.setInt(4, poId);
            stmt.setString(5, WorkRequestType.PURCHASE_ORDER.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating work_request status for purchase order");
            e.printStackTrace();
        }
    }

    public void createShipmentForPurchaseOrder(int poId, int createdByUserId, String comments) {
        String sql = "INSERT INTO work_request "
                + "(type, status, created_at, updated_at, created_by_user_id, related_po_id, comments) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, WorkRequestType.SHIPMENT.name());
            stmt.setString(2, WorkRequestStatus.OPEN.name());  // shipment started
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));
            stmt.setInt(5, createdByUserId);
            stmt.setInt(6, poId);
            stmt.setString(7, comments);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error creating SHIPMENT work_request for purchase order");
            e.printStackTrace();
        }
    }

    public void updateShipmentStatusForPurchaseOrder(int poId, WorkRequestStatus newStatus, String comments) {
        String sql = "UPDATE work_request "
                + "SET status = ?, updated_at = ?, comments = ? "
                + "WHERE related_po_id = ? AND type = ?";

        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = DBConnectionUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setTimestamp(2, Timestamp.valueOf(now));
            stmt.setString(3, comments);
            stmt.setInt(4, poId);
            stmt.setString(5, WorkRequestType.SHIPMENT.name());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating SHIPMENT work_request for purchase order");
            e.printStackTrace();
        }
    }
    
    // ADD these methods to your existing WorkRequestDao.java

    // Find all work requests with joined info
    public List<WorkRequest> findAll() {
        List<WorkRequest> list = new ArrayList<>();
        String sql = "SELECT id, type, status, created_at, updated_at, " +
                     "created_by_user_id, related_po_id, comments " +
                     "FROM work_request ORDER BY created_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                WorkRequest wr = mapRow(rs);
                list.add(wr);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all work requests");
            e.printStackTrace();
        }

        return list;
    }

    // Find work requests by user (creator)
    public List<WorkRequest> findByUserId(int userId) {
        List<WorkRequest> list = new ArrayList<>();
        String sql = "SELECT id, type, status, created_at, updated_at, " +
                     "created_by_user_id, related_po_id, comments " +
                     "FROM work_request WHERE created_by_user_id = ? " +
                     "ORDER BY created_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WorkRequest wr = mapRow(rs);
                    list.add(wr);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching work requests for userId=" + userId);
            e.printStackTrace();
        }

        return list;
    }

    // Find work requests by purchase order
    public List<WorkRequest> findByPurchaseOrderId(int poId) {
        List<WorkRequest> list = new ArrayList<>();
        String sql = "SELECT id, type, status, created_at, updated_at, " +
                     "created_by_user_id, related_po_id, comments " +
                     "FROM work_request WHERE related_po_id = ? " +
                     "ORDER BY created_at DESC";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, poId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    WorkRequest wr = mapRow(rs);
                    list.add(wr);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching work requests for poId=" + poId);
            e.printStackTrace();
        }

        return list;
    }

    private WorkRequest mapRow(ResultSet rs) throws SQLException {
        WorkRequest wr = new WorkRequest();
        wr.setId(rs.getInt("id"));
        wr.setType(WorkRequestType.valueOf(rs.getString("type")));
        wr.setStatus(WorkRequestStatus.valueOf(rs.getString("status")));

        Timestamp ct = rs.getTimestamp("created_at");
        if (ct != null) wr.setCreatedAt(ct.toLocalDateTime());

        Timestamp ut = rs.getTimestamp("updated_at");
        if (ut != null) wr.setUpdatedAt(ut.toLocalDateTime());

        wr.setCreatedByUserId(rs.getInt("created_by_user_id"));

        int poId = rs.getInt("related_po_id");
        if (!rs.wasNull()) {
            wr.setRelatedPurchaseOrderId(poId);
        }

        wr.setComments(rs.getString("comments"));
        return wr;
    }

}
