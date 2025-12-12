package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.TherapistProfile;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TherapistProfileDao {

    public List<TherapistProfile> findByBranch(int branchId) {
        List<TherapistProfile> list = new ArrayList<>();
        String sql = "SELECT id, user_id, branch_id, specialty " +
                     "FROM therapist_profile WHERE branch_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, branchId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching therapist_profile for branchId=" + branchId);
            e.printStackTrace();
        }

        return list;
    }

    public TherapistProfile findByUserId(int userId) {
        String sql = "SELECT id, user_id, branch_id, specialty " +
                     "FROM therapist_profile WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching therapist_profile for userId=" + userId);
            e.printStackTrace();
        }

        return null;
    }

    // NEW: Find all therapist profiles
    public List<TherapistProfile> findAll() {
        List<TherapistProfile> list = new ArrayList<>();
        String sql = "SELECT id, user_id, branch_id, specialty FROM therapist_profile";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all therapist profiles");
            e.printStackTrace();
        }

        return list;
    }

    // NEW: Create therapist profile
    public void create(TherapistProfile profile) {
        String sql = "INSERT INTO therapist_profile (user_id, branch_id, specialty) " +
                     "VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, profile.getUserId());
            stmt.setInt(2, profile.getBranchId());
            stmt.setString(3, profile.getSpecialty());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows inserted for therapist_profile");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    profile.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting therapist_profile");
            e.printStackTrace();
        }
    }

    // NEW: Update therapist profile
    public void update(TherapistProfile profile) {
        String sql = "UPDATE therapist_profile SET user_id = ?, branch_id = ?, specialty = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profile.getUserId());
            stmt.setInt(2, profile.getBranchId());
            stmt.setString(3, profile.getSpecialty());
            stmt.setInt(4, profile.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows updated for therapist_profile id=" + profile.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating therapist_profile");
            e.printStackTrace();
        }
    }

    // NEW: Delete therapist profile
    public void delete(int id) {
        String sql = "DELETE FROM therapist_profile WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows deleted for therapist_profile id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting therapist_profile");
            e.printStackTrace();
        }
    }

    // NEW: Delete by user ID (when deleting a user account)
    public void deleteByUserId(int userId) {
        String sql = "DELETE FROM therapist_profile WHERE user_id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting therapist_profile by userId");
            e.printStackTrace();
        }
    }

    private TherapistProfile mapRow(ResultSet rs) throws SQLException {
        TherapistProfile t = new TherapistProfile();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setBranchId(rs.getInt("branch_id"));
        t.setSpecialty(rs.getString("specialty"));
        return t;
    }
}