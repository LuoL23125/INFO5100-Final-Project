package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import java.awt.*;

public class TherapistDashboardFrame extends JFrame {

    private final UserAccount currentUser;

    public TherapistDashboardFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Therapist Dashboard - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
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

        JButton btnMyAppointments = new JButton("View My Appointments");
        JButton btnLogout = new JButton("Logout");

        btnMyAppointments.addActionListener(e -> {
            TherapistAppointmentsFrame frame = new TherapistAppointmentsFrame(currentUser);
            frame.setVisible(true);
        });

        btnLogout.addActionListener(e -> onLogout());

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblWelcome, gbc);

        gbc.gridy++;
        panel.add(btnMyAppointments, gbc);

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
