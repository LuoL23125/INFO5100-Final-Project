package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.ui.AppointmentManagementFrame;





import javax.swing.*;
import java.awt.*;

public class BranchAdminDashboardFrame extends JFrame {

    private final UserAccount currentUser;

    public BranchAdminDashboardFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Branch Admin Dashboard - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblWelcome = new JLabel("Welcome, " +
                (currentUser.getFullName() != null ? currentUser.getFullName() : currentUser.getUsername()));
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(Font.BOLD, 14f));

        JButton btnManageBranches = new JButton("Manage Clinic Branches");
        JButton btnManageCustomers = new JButton("Manage Customers");
        JButton btnLogout = new JButton("Logout");
        JButton btnManageInventory = new JButton("Manage Inventory");
        JButton btnManagePOs = new JButton("Manage Purchase Orders");
        JButton btnManageAppointments = new JButton("Manage Appointments");
        
        btnManageAppointments.addActionListener(e -> {
        AppointmentManagementFrame frame = new AppointmentManagementFrame(currentUser);
        frame.setVisible(true);
        });

        
    
        
        btnManagePOs.addActionListener(e -> {
        PurchaseOrderManagementFrame frame = new PurchaseOrderManagementFrame(currentUser);
        frame.setVisible(true);
        });

        btnManageBranches.addActionListener(e -> {
            BranchManagementFrame frame = new BranchManagementFrame();
            frame.setVisible(true);
        });

        btnManageCustomers.addActionListener(e -> {
            CustomerManagementFrame frame = new CustomerManagementFrame();
            frame.setVisible(true);
        });

        btnLogout.addActionListener(e -> onLogout());
        
        btnManageInventory.addActionListener(e -> {
        BranchInventoryFrame frame = new BranchInventoryFrame();
        frame.setVisible(true);
        });

        gbc.gridx = 0; 
        gbc.gridy = 0;
        panel.add(lblWelcome, gbc);

        gbc.gridy++;
        panel.add(btnManageBranches, gbc);

        gbc.gridy++;
        panel.add(btnManageCustomers, gbc);
        
        gbc.gridy++;
        panel.add(btnManageInventory, gbc);
        
        gbc.gridy++;
        panel.add(btnManagePOs, gbc);
        
        gbc.gridy++;
        panel.add(btnManageAppointments, gbc);

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
            // Close this dashboard
            dispose();

            // Return to login screen
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
