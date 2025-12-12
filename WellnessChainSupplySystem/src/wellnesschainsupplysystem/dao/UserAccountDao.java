package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.Role;
import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDao {

    public UserAccount findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, username, password, role, full_name, email " +
                     "FROM user_account WHERE username = ? AND password = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username/password");
            e.printStackTrace();
        }

        return null;
    }

    public UserAccount findById(int id) {
        String sql = "SELECT id, username, password, role, full_name, email " +
                     "FROM user_account WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by id");
            e.printStackTrace();
        }

        return null;
    }

    // NEW: Find all users
    public List<UserAccount> findAll() {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role, full_name, email FROM user_account";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching all users");
            e.printStackTrace();
        }

        return users;
    }

    // NEW: Find users by role
    public List<UserAccount> findByRole(Role role) {
        List<UserAccount> users = new ArrayList<>();
        String sql = "SELECT id, username, password, role, full_name, email " +
                     "FROM user_account WHERE role = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching users by role");
            e.printStackTrace();
        }

        return users;
    }

    // NEW: Create user
    public void create(UserAccount user) {
        String sql = "INSERT INTO user_account (username, password, role, full_name, email) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows inserted for user_account");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting user_account");
            e.printStackTrace();
        }
    }

    // NEW: Update user
    public void update(UserAccount user) {
        String sql = "UPDATE user_account SET username = ?, password = ?, role = ?, " +
                     "full_name = ?, email = ? WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole().name());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setInt(6, user.getId());

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows updated for user_account id=" + user.getId());
            }

        } catch (SQLException e) {
            System.err.println("Error updating user_account");
            e.printStackTrace();
        }
    }

    // NEW: Delete user
    public void delete(int id) {
        String sql = "DELETE FROM user_account WHERE id = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affected = stmt.executeUpdate();
            if (affected == 0) {
                System.out.println("⚠️ No rows deleted for user_account id=" + id);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting user_account");
            e.printStackTrace();
        }
    }

    // NEW: Check if username exists
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM user_account WHERE username = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking username existence");
            e.printStackTrace();
        }

        return false;
    }

    private UserAccount mapRow(ResultSet rs) throws SQLException {
        UserAccount user = new UserAccount();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        String roleStr = rs.getString("role");
        try {
            user.setRole(Role.valueOf(roleStr));
        } catch (IllegalArgumentException ex) {
            System.err.println("Unknown role in DB: " + roleStr);
            user.setRole(null);
        }
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        return user;
    }
}