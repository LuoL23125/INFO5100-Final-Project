package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import java.awt.*;

public class SupplierAdminDashboardFrame extends JFrame {

    private final UserAccount currentUser;

    public SupplierAdminDashboardFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Supplier Admin Dashboard - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);  // Increased height
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblWelcome = new JLabel("Welcome, " +
                (currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername()));
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnManageProducts = new JButton("Manage Products");
        JButton btnViewPOs = new JButton("View Purchase Orders");
        JButton btnViewWorkRequests = new JButton("View Work Requests");  // NEW
        JButton btnLogout = new JButton("Logout");

        btnManageProducts.addActionListener(e -> {
            ProductManagementFrame frame = new ProductManagementFrame();
            frame.setVisible(true);
        });

        btnViewPOs.addActionListener(e -> {
            SupplierPurchaseOrderFrame frame = new SupplierPurchaseOrderFrame(currentUser);
            frame.setVisible(true);
        });

        // NEW: View Work Requests
        btnViewWorkRequests.addActionListener(e -> {
            WorkRequestViewFrame frame = new WorkRequestViewFrame(currentUser);
            frame.setVisible(true);
        });

        btnLogout.addActionListener(e -> onLogout());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblWelcome, gbc);

        gbc.gridy++;
        panel.add(btnManageProducts, gbc);

        gbc.gridy++;
        panel.add(btnViewPOs, gbc);

        gbc.gridy++;
        panel.add(btnViewWorkRequests, gbc);  // NEW

        gbc.gridy++;
        panel.add(btnLogout, gbc);

        add(panel);
    }

    private void onLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}