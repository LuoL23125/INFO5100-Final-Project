package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.ClinicBranch;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClinicBranchDao {

    public void create(ClinicBranch branch) {
        String sql = "INSERT INTO clinic_branch (name, address, city, opening_time, closing_time) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getCity());
            stmt.setString(4, branch.getOpeningTime());
            stmt.setString(5, branch.getClosingTime());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows inserted for clinic_branch");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    branch.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting clinic_branch");
            e.printStackTrace();
        }
    }

    public List<ClinicBranch> findAll() {
        List<ClinicBranch> branches = new ArrayList<>();
        String sql = "SELECT id, name, address, city, opening_time, closing_time FROM clinic_branch";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ClinicBranch b = new ClinicBranch();
                b.setId(rs.getInt("id"));
                b.setName(rs.getString("name"));
                b.setAddress(rs.getString("address"));
                b.setCity(rs.getString("city"));
                b.setOpeningTime(rs.getString("opening_time"));
                b.setClosingTime(rs.getString("closing_time"));
                branches.add(b);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching clinic_branch records");
            e.printStackTrace();
        }

        return branches;
    }

    public void update(ClinicBranch branch) {
        String sql = "UPDATE clinic_branch " +
                     "SET name = ?, address = ?, city = ?, opening_time = ?, closing_time = ? " +
                     "WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branch.getName());
            stmt.setString(2, branch.getAddress());
            stmt.setString(3, branch.getCity());
            stmt.setString(4, branch.getOpeningTime());
            stmt.setString(5, branch.getClosingTime());
            stmt.setInt(6, branch.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows updated for clinic_branch id=" + branch.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating clinic_branch");
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM clinic_branch WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows deleted for clinic_branch id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting clinic_branch");
            e.printStackTrace();
        }
    }
}
