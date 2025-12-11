package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.AppointmentDao;
import wellnesschainsupplysystem.model.Appointment;
import wellnesschainsupplysystem.model.AppointmentStatus;
import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TherapistAppointmentsFrame extends JFrame {

    private final UserAccount currentUser;
    private final AppointmentDao appointmentDao = new AppointmentDao();

    private JTable apptTable;
    private DefaultTableModel tableModel;

    private JButton btnMarkCompleted;
    private JButton btnMarkCancelled;
    private JButton btnRefresh;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TherapistAppointmentsFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("My Appointments - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadAppointments();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Branch", "Customer", "Start", "End", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        apptTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(apptTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnMarkCompleted = new JButton("Mark Completed");
        btnMarkCancelled = new JButton("Mark Cancelled");
        btnRefresh = new JButton("Refresh");

        btnMarkCompleted.addActionListener(e -> updateStatus(AppointmentStatus.COMPLETED));
        btnMarkCancelled.addActionListener(e -> updateStatus(AppointmentStatus.CANCELLED));
        btnRefresh.addActionListener(e -> loadAppointments());

        buttonPanel.add(btnMarkCompleted);
        buttonPanel.add(btnMarkCancelled);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAppointments() {
        tableModel.setRowCount(0);
        List<Appointment> appts = appointmentDao.findByTherapistUserId(currentUser.getId());
        for (Appointment ap : appts) {
            String startStr = ap.getStartTime() != null ? ap.getStartTime().format(formatter) : "";
            String endStr = ap.getEndTime() != null ? ap.getEndTime().format(formatter) : "";
            tableModel.addRow(new Object[]{
                    ap.getId(),
                    ap.getBranchName(),
                    ap.getCustomerName(),
                    startStr,
                    endStr,
                    ap.getStatus()
            });
        }
    }

    private Integer getSelectedApptId() {
        int row = apptTable.getSelectedRow();
        if (row < 0) return null;
        Object value = tableModel.getValueAt(row, 0);
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private void updateStatus(AppointmentStatus newStatus) {
        Integer apptId = getSelectedApptId();
        if (apptId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an appointment.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        appointmentDao.updateStatus(apptId, newStatus);

        JOptionPane.showMessageDialog(this,
                "Appointment updated to " + newStatus,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadAppointments();
    }
}
