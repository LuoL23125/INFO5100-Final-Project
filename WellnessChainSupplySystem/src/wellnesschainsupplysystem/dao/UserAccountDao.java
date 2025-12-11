package wellnesschainsupplysystem.dao;

import wellnesschainsupplysystem.model.Role;
import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.util.DBConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    UserAccount user = new UserAccount();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));

                    String roleStr = rs.getString("role");
                    try {
                        Role role = Role.valueOf(roleStr);
                        user.setRole(role);
                    } catch (IllegalArgumentException ex) {
                        System.err.println("Unknown role in DB: " + roleStr);
                        user.setRole(null);
                    }

                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching user by username/password");
            e.printStackTrace();
        }

        return null; // no user found
    }
}
