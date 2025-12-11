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
                    TherapistProfile t = new TherapistProfile();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setBranchId(rs.getInt("branch_id"));
                    t.setSpecialty(rs.getString("specialty"));
                    list.add(t);
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
                    TherapistProfile t = new TherapistProfile();
                    t.setId(rs.getInt("id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setBranchId(rs.getInt("branch_id"));
                    t.setSpecialty(rs.getString("specialty"));
                    return t;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching therapist_profile for userId=" + userId);
            e.printStackTrace();
        }

        return null;
    }
}
