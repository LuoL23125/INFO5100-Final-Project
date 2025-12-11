package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.Appointment;
import wellnesschainsupplysystem.model.AppointmentStatus;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    public void create(Appointment appt) {
        String sql = "INSERT INTO appointment " +
                "(branch_id, therapist_user_id, customer_id, start_time, end_time, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, appt.getBranchId());
            stmt.setInt(2, appt.getTherapistUserId());
            stmt.setInt(3, appt.getCustomerId());
            stmt.setTimestamp(4, Timestamp.valueOf(appt.getStartTime()));
            stmt.setTimestamp(5, Timestamp.valueOf(appt.getEndTime()));
            stmt.setString(6, appt.getStatus().name());
            stmt.setString(7, appt.getNotes());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    appt.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting appointment");
            e.printStackTrace();
        }
    }

    public List<Appointment> findByBranch(int branchId) {
        List<Appointment> list = new ArrayList<>();

        String sql = "SELECT a.id, a.branch_id, a.therapist_user_id, a.customer_id, " +
                "a.start_time, a.end_time, a.status, a.notes, " +
                "b.name AS branch_name, ua.full_name AS therapist_name, c.name AS customer_name " +
                "FROM appointment a " +
                "JOIN clinic_branch b ON a.branch_id = b.id " +
                "JOIN user_account ua ON a.therapist_user_id = ua.id " +
                "JOIN customer c ON a.customer_id = c.id " +
                "WHERE a.branch_id = ? " +
                "ORDER BY a.start_time";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment ap = mapRow(rs);
                    list.add(ap);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching appointments for branchId=" + branchId);
            e.printStackTrace();
        }

        return list;
    }

    public List<Appointment> findByTherapistUserId(int therapistUserId) {
        List<Appointment> list = new ArrayList<>();

        String sql = "SELECT a.id, a.branch_id, a.therapist_user_id, a.customer_id, " +
                "a.start_time, a.end_time, a.status, a.notes, " +
                "b.name AS branch_name, ua.full_name AS therapist_name, c.name AS customer_name " +
                "FROM appointment a " +
                "JOIN clinic_branch b ON a.branch_id = b.id " +
                "JOIN user_account ua ON a.therapist_user_id = ua.id " +
                "JOIN customer c ON a.customer_id = c.id " +
                "WHERE a.therapist_user_id = ? " +
                "ORDER BY a.start_time";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, therapistUserId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Appointment ap = mapRow(rs);
                    list.add(ap);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching appointments for therapistUserId=" + therapistUserId);
            e.printStackTrace();
        }

        return list;
    }

    public void updateStatus(int apptId, AppointmentStatus newStatus) {
        String sql = "UPDATE appointment SET status = ? WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus.name());
            stmt.setInt(2, apptId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating appointment status");
            e.printStackTrace();
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Appointment ap = new Appointment();
        ap.setId(rs.getInt("id"));
        ap.setBranchId(rs.getInt("branch_id"));
        ap.setTherapistUserId(rs.getInt("therapist_user_id"));
        ap.setCustomerId(rs.getInt("customer_id"));

        Timestamp st = rs.getTimestamp("start_time");
        if (st != null) ap.setStartTime(st.toLocalDateTime());

        Timestamp et = rs.getTimestamp("end_time");
        if (et != null) ap.setEndTime(et.toLocalDateTime());

        ap.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        ap.setNotes(rs.getString("notes"));

        ap.setBranchName(rs.getString("branch_name"));
        ap.setTherapistName(rs.getString("therapist_name"));
        ap.setCustomerName(rs.getString("customer_name"));
        return ap;
    }
}
