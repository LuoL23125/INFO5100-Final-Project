package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.ClinicBranchDao;
import wellnesschainsupplysystem.dao.TherapistProfileDao;
import wellnesschainsupplysystem.dao.UserAccountDao;
import wellnesschainsupplysystem.model.ClinicBranch;
import wellnesschainsupplysystem.model.Role;
import wellnesschainsupplysystem.model.TherapistProfile;
import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TherapistManagementFrame extends JFrame {

    private final UserAccountDao userDao = new UserAccountDao();
    private final TherapistProfileDao profileDao = new TherapistProfileDao();
    private final ClinicBranchDao branchDao = new ClinicBranchDao();

    private JTable therapistTable;
    private DefaultTableModel tableModel;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtFullName;
    private JTextField txtEmail;
    private JTextField txtSpecialty;
    private JComboBox<ClinicBranch> cbBranch;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    public TherapistManagementFrame() {
        setTitle("Therapist Management - Branch Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadBranches();
        loadTherapists();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"User ID", "Username", "Full Name", "Email", "Branch", "Specialty"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        therapistTable = new JTable(tableModel);
        therapistTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        therapistTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(therapistTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Therapist Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");
        JLabel lblFullName = new JLabel("Full Name:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblBranch = new JLabel("Branch:");
        JLabel lblSpecialty = new JLabel("Specialty:");

        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtFullName = new JTextField(15);
        txtEmail = new JTextField(15);
        cbBranch = new JComboBox<>();
        txtSpecialty = new JTextField(15);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblUsername, gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);
        gbc.gridx = 2;
        formPanel.add(lblPassword, gbc);
        gbc.gridx = 3;
        formPanel.add(txtPassword, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblFullName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtFullName, gbc);
        gbc.gridx = 2;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 3;
        formPanel.add(txtEmail, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblBranch, gbc);
        gbc.gridx = 1;
        formPanel.add(cbBranch, gbc);
        gbc.gridx = 2;
        formPanel.add(lblSpecialty, gbc);
        gbc.gridx = 3;
        formPanel.add(txtSpecialty, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add Therapist");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadTherapists());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBranches() {
        cbBranch.removeAllItems();
        List<ClinicBranch> branches = branchDao.findAll();
        for (ClinicBranch b : branches) {
            cbBranch.addItem(b);
        }
    }

    private void loadTherapists() {
        tableModel.setRowCount(0);

        List<UserAccount> therapists = userDao.findByRole(Role.THERAPIST);
        for (UserAccount user : therapists) {
            TherapistProfile profile = profileDao.findByUserId(user.getId());
            String branchName = "";
            String specialty = "";

            if (profile != null) {
                specialty = profile.getSpecialty();
                // Find branch name
                List<ClinicBranch> branches = branchDao.findAll();
                for (ClinicBranch b : branches) {
                    if (b.getId() == profile.getBranchId()) {
                        branchName = b.getName();
                        break;
                    }
                }
            }

            tableModel.addRow(new Object[]{
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    branchName,
                    specialty
            });
        }

        clearForm();
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtFullName.setText("");
        txtEmail.setText("");
        txtSpecialty.setText("");
        if (cbBranch.getItemCount() > 0) {
            cbBranch.setSelectedIndex(0);
        }
        therapistTable.clearSelection();
        txtUsername.setEditable(true);
    }

    private void onTableRowSelected() {
        int row = therapistTable.getSelectedRow();
        if (row >= 0) {
            int userId = (int) tableModel.getValueAt(row, 0);
            UserAccount user = userDao.findById(userId);
            TherapistProfile profile = profileDao.findByUserId(userId);

            if (user != null) {
                txtUsername.setText(user.getUsername());
                txtUsername.setEditable(false); // Don't allow username change
                txtPassword.setText(""); // Don't show password
                txtFullName.setText(user.getFullName());
                txtEmail.setText(user.getEmail());

                if (profile != null) {
                    txtSpecialty.setText(profile.getSpecialty());
                    // Select branch in combo box
                    for (int i = 0; i < cbBranch.getItemCount(); i++) {
                        ClinicBranch b = cbBranch.getItemAt(i);
                        if (b.getId() == profile.getBranchId()) {
                            cbBranch.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private Integer getSelectedUserId() {
        int row = therapistTable.getSelectedRow();
        if (row < 0) return null;
        return (Integer) tableModel.getValueAt(row, 0);
    }

    private void onAdd() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String specialty = txtSpecialty.getText().trim();
        ClinicBranch branch = (ClinicBranch) cbBranch.getSelectedItem();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username, Password, and Full Name are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (branch == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a branch.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if username exists
        if (userDao.usernameExists(username)) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists. Please choose another.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create user account
        UserAccount user = new UserAccount();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(Role.THERAPIST);
        user.setFullName(fullName);
        user.setEmail(email);
        userDao.create(user);

        // Create therapist profile
        TherapistProfile profile = new TherapistProfile();
        profile.setUserId(user.getId());
        profile.setBranchId(branch.getId());
        profile.setSpecialty(specialty);
        profileDao.create(profile);

        JOptionPane.showMessageDialog(this,
                "Therapist added: " + fullName,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadTherapists();
    }

    private void onUpdate() {
        Integer userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a therapist to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String specialty = txtSpecialty.getText().trim();
        ClinicBranch branch = (ClinicBranch) cbBranch.getSelectedItem();

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Full Name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Update user account
        UserAccount user = userDao.findById(userId);
        if (user != null) {
            if (!password.isEmpty()) {
                user.setPassword(password); // Only update if new password provided
            }
            user.setFullName(fullName);
            user.setEmail(email);
            userDao.update(user);
        }

        // Update therapist profile
        TherapistProfile profile = profileDao.findByUserId(userId);
        if (profile != null && branch != null) {
            profile.setBranchId(branch.getId());
            profile.setSpecialty(specialty);
            profileDao.update(profile);
        }

        JOptionPane.showMessageDialog(this,
                "Therapist updated.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadTherapists();
    }

    private void onDelete() {
        Integer userId = getSelectedUserId();
        if (userId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a therapist to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this therapist?\n" +
                "This will also delete their profile and appointments.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // Delete profile first (foreign key constraint)
            profileDao.deleteByUserId(userId);
            // Then delete user account
            userDao.delete(userId);

            JOptionPane.showMessageDialog(this,
                    "Therapist deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadTherapists();
        }
    }
}