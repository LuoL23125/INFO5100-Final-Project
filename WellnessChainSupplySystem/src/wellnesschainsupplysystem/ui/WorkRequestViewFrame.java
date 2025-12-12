package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.UserAccountDao;
import wellnesschainsupplysystem.dao.WorkRequestDao;
import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.model.WorkRequest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class WorkRequestViewFrame extends JFrame {

    private final UserAccount currentUser;
    private final WorkRequestDao workRequestDao = new WorkRequestDao();
    private final UserAccountDao userAccountDao = new UserAccountDao();

    private JTable wrTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JTextArea txtComments;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public WorkRequestViewFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Work Requests - " + currentUser.getRole());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadWorkRequests();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Type", "Status", "Created At", "Updated At", "Created By", "PO ID"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wrTable = new JTable(tableModel);
        wrTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wrTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(wrTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Bottom panel: comments + refresh
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel commentsPanel = new JPanel(new BorderLayout());
        commentsPanel.setBorder(BorderFactory.createTitledBorder("Comments"));
        txtComments = new JTextArea(4, 50);
        txtComments.setEditable(false);
        txtComments.setLineWrap(true);
        txtComments.setWrapStyleWord(true);
        JScrollPane commentsScroll = new JScrollPane(txtComments);
        commentsPanel.add(commentsScroll, BorderLayout.CENTER);

        bottomPanel.add(commentsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadWorkRequests());
        buttonPanel.add(btnRefresh);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadWorkRequests() {
        tableModel.setRowCount(0);

        List<WorkRequest> workRequests = workRequestDao.findAll();

        for (WorkRequest wr : workRequests) {
            String createdAt = wr.getCreatedAt() != null ? wr.getCreatedAt().format(formatter) : "";
            String updatedAt = wr.getUpdatedAt() != null ? wr.getUpdatedAt().format(formatter) : "";

            // Get creator name
            String creatorName = "Unknown";
            UserAccount creator = userAccountDao.findById(wr.getCreatedByUserId());
            if (creator != null) {
                creatorName = creator.getFullName() != null ? creator.getFullName() : creator.getUsername();
            }

            String poIdStr = wr.getRelatedPurchaseOrderId() != null ? 
                             String.valueOf(wr.getRelatedPurchaseOrderId()) : "-";

            tableModel.addRow(new Object[]{
                    wr.getId(),
                    wr.getType(),
                    wr.getStatus(),
                    createdAt,
                    updatedAt,
                    creatorName,
                    poIdStr
            });
        }

        txtComments.setText("");
    }

    private void onTableRowSelected() {
        int row = wrTable.getSelectedRow();
        if (row >= 0) {
            int wrId = (int) tableModel.getValueAt(row, 0);
            
            // Find the work request and display comments
            List<WorkRequest> all = workRequestDao.findAll();
            for (WorkRequest wr : all) {
                if (wr.getId() == wrId) {
                    txtComments.setText(wr.getComments() != null ? wr.getComments() : "(No comments)");
                    break;
                }
            }
        }
    }
}