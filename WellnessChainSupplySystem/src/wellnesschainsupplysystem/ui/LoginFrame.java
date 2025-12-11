package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.UserAccountDao;
import wellnesschainsupplysystem.model.Role;
import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    private final UserAccountDao userAccountDao = new UserAccountDao();

    public LoginFrame() {
        setTitle("Login - Wellness Chain System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel lblTitle = new JLabel("Wellness Chain Supply System");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> onLogin());

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(lblUsername, gbc);
        gbc.gridx = 1;
        panel.add(txtUsername, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(lblPassword, gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogin, gbc);

        add(panel);
    }

    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserAccount user = userAccountDao.findByUsernameAndPassword(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Welcome, " + (user.getFullName() != null ? user.getFullName() : user.getUsername()) +
                        " (" + user.getRole() + ")",
                "Login Successful",
                JOptionPane.INFORMATION_MESSAGE);

        // For now: if role is BRANCH_ADMIN -> open CustomerManagementFrame
        // Later we'll branch to different dashboards.
        openDashboardForUser(user);
    }

    private void openDashboardForUser(UserAccount user) {
        Role role = user.getRole();

        if (role == Role.BRANCH_ADMIN) {
            // Open Customer Management UI for branch admin
            CustomerManagementFrame frame = new CustomerManagementFrame();
            frame.setVisible(true);
        } else {
            // For now, just show a placeholder
            JOptionPane.showMessageDialog(this,
                    "Role " + role + " is not wired to a specific dashboard yet.\n" +
                    "For now, only BRANCH_ADMIN opens the customer UI.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Close the login window
        dispose();
    }
}
