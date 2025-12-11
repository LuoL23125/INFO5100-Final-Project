package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.AppointmentDao;
import wellnesschainsupplysystem.dao.ClinicBranchDao;
import wellnesschainsupplysystem.dao.CustomerDao;
import wellnesschainsupplysystem.dao.TherapistProfileDao;
import wellnesschainsupplysystem.dao.UserAccountDao;
import wellnesschainsupplysystem.model.Appointment;
import wellnesschainsupplysystem.model.AppointmentStatus;
import wellnesschainsupplysystem.model.ClinicBranch;
import wellnesschainsupplysystem.model.Customer;
import wellnesschainsupplysystem.model.TherapistProfile;
import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentManagementFrame extends JFrame {

    private final UserAccount currentUser;

    private final ClinicBranchDao branchDao = new ClinicBranchDao();
    private final TherapistProfileDao therapistDao = new TherapistProfileDao();
    private final CustomerDao customerDao = new CustomerDao();
    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final UserAccountDao userAccountDao = new UserAccountDao();

    private JComboBox<ClinicBranch> cbBranch;
    private JComboBox<UserAccount> cbTherapist;
    private JComboBox<Customer> cbCustomer;
    private JTextField txtStart;
    private JTextField txtEnd;
    private JTextField txtNotes;

    private JTable apptTable;
    private DefaultTableModel tableModel;

    private JButton btnCreate;
    private JButton btnRefresh;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ✅ THIS is the constructor NetBeans was complaining about
    public AppointmentManagementFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Appointment Management - Branch Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadBranches();
        loadTherapistsForSelectedBranch();
        loadCustomers();
        loadAppointmentsForSelectedBranch();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top: branch selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBranch = new JLabel("Branch:");
        cbBranch = new JComboBox<>();
        cbBranch.addActionListener(e -> {
            loadTherapistsForSelectedBranch();
            loadAppointmentsForSelectedBranch();
        });
        topPanel.add(lblBranch);
        topPanel.add(cbBranch);
        add(topPanel, BorderLayout.NORTH);

        // Center: appointments table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Branch", "Therapist", "Customer", "Start", "End", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        apptTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(apptTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: form to create new appointment
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Appointment"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTherapist = new JLabel("Therapist:");
        JLabel lblCustomer = new JLabel("Customer:");
        JLabel lblStart = new JLabel("Start (yyyy-MM-dd HH:mm):");
        JLabel lblEnd = new JLabel("End (yyyy-MM-dd HH:mm):");
        JLabel lblNotes = new JLabel("Notes:");

        cbTherapist = new JComboBox<>();
        cbCustomer = new JComboBox<>();
        txtStart = new JTextField(16);
        txtEnd = new JTextField(16);
        txtNotes = new JTextField(20);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblTherapist, gbc);
        gbc.gridx = 1;
        formPanel.add(cbTherapist, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblCustomer, gbc);
        gbc.gridx = 1;
        formPanel.add(cbCustomer, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblStart, gbc);
        gbc.gridx = 1;
        formPanel.add(txtStart, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblEnd, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEnd, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblNotes, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNotes, gbc);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreate = new JButton("Create Appointment");
        btnRefresh = new JButton("Refresh");

        btnCreate.addActionListener(e -> onCreateAppointment());
        btnRefresh.addActionListener(e -> loadAppointmentsForSelectedBranch());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnRefresh);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadBranches() {
        cbBranch.removeAllItems();
        List<ClinicBranch> branches = branchDao.findAll();
        for (ClinicBranch b : branches) {
            cbBranch.addItem(b);
        }
    }

    private void loadTherapistsForSelectedBranch() {
        cbTherapist.removeAllItems();
        ClinicBranch branch = (ClinicBranch) cbBranch.getSelectedItem();
        if (branch == null) return;

        List<TherapistProfile> profiles = therapistDao.findByBranch(branch.getId());

        for (TherapistProfile tp : profiles) {
            UserAccount ua = userAccountDao.findById(tp.getUserId());
            if (ua != null) {
                cbTherapist.addItem(ua);
            }
        }
    }

    private void loadCustomers() {
        cbCustomer.removeAllItems();
        List<Customer> customers = customerDao.findAll();
        for (Customer c : customers) {
            cbCustomer.addItem(c);
        }
    }

    private void loadAppointmentsForSelectedBranch() {
        ClinicBranch branch = (ClinicBranch) cbBranch.getSelectedItem();
        tableModel.setRowCount(0);
        if (branch == null) return;

        List<Appointment> appts = appointmentDao.findByBranch(branch.getId());
        for (Appointment ap : appts) {
            String startStr = ap.getStartTime() != null ? ap.getStartTime().format(formatter) : "";
            String endStr = ap.getEndTime() != null ? ap.getEndTime().format(formatter) : "";
            tableModel.addRow(new Object[]{
                    ap.getId(),
                    ap.getBranchName(),
                    ap.getTherapistName(),
                    ap.getCustomerName(),
                    startStr,
                    endStr,
                    ap.getStatus()
            });
        }
    }

    private void onCreateAppointment() {
        ClinicBranch branch = (ClinicBranch) cbBranch.getSelectedItem();
        UserAccount therapist = (UserAccount) cbTherapist.getSelectedItem();
        Customer customer = (Customer) cbCustomer.getSelectedItem();

        if (branch == null || therapist == null || customer == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select branch, therapist, and customer.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String startStr = txtStart.getText().trim();
        String endStr = txtEnd.getText().trim();
        String notes = txtNotes.getText().trim();

        LocalDateTime start, end;
        try {
            start = LocalDateTime.parse(startStr, formatter);
            end = LocalDateTime.parse(endStr, formatter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date/time format. Use yyyy-MM-dd HH:mm",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!end.isAfter(start)) {
            JOptionPane.showMessageDialog(this,
                    "End time must be after start time.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Appointment ap = new Appointment();
        ap.setBranchId(branch.getId());
        ap.setTherapistUserId(therapist.getId());
        ap.setCustomerId(customer.getId());
        ap.setStartTime(start);
        ap.setEndTime(end);
        ap.setStatus(AppointmentStatus.SCHEDULED);
        ap.setNotes(notes);

        appointmentDao.create(ap);

        JOptionPane.showMessageDialog(this,
                "Appointment created with ID: " + ap.getId(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        txtStart.setText("");
        txtEnd.setText("");
        txtNotes.setText("");

        loadAppointmentsForSelectedBranch();
    }
}
