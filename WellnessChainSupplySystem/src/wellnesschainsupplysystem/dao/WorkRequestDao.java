package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.WorkRequest;
import wellnesschainsupplysystem.model.WorkRequestStatus;
import wellnesschainsupplysystem.model.WorkRequestType;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;

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

}
