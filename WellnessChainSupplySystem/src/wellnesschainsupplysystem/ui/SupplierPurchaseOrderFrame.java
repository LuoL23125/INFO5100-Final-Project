package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.PurchaseOrderDao;
import wellnesschainsupplysystem.dao.WorkRequestDao;
import wellnesschainsupplysystem.model.PurchaseOrder;
import wellnesschainsupplysystem.model.PurchaseOrderStatus;
import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.model.WorkRequestStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPurchaseOrderFrame extends JFrame {

    private final UserAccount currentUser;
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final WorkRequestDao wrDao = new WorkRequestDao();

    private JTable poTable;
    private DefaultTableModel tableModel;

    private JButton btnApprove;
    private JButton btnReject;
    private JButton btnRefresh;

    public SupplierPurchaseOrderFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Purchase Orders - Supplier Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadAllPOs();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Branch", "Product", "Quantity", "Status"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        poTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(poTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnApprove = new JButton("Approve");
        btnReject = new JButton("Reject");
        btnRefresh = new JButton("Refresh");

        btnApprove.addActionListener(e -> onApprove());
        btnReject.addActionListener(e -> onReject());
        btnRefresh.addActionListener(e -> loadAllPOs());

        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadAllPOs() {
        tableModel.setRowCount(0);
        List<PurchaseOrder> pos = poDao.findAllWithSummary();
        for (PurchaseOrder po : pos) {
            tableModel.addRow(new Object[]{
                    po.getId(),
                    po.getBranchName(),
                    po.getProductName(),
                    po.getQuantity(),
                    po.getStatus()
            });
        }
    }

    private Integer getSelectedPoId() {
        int row = poTable.getSelectedRow();
        if (row < 0) return null;
        Object value = tableModel.getValueAt(row, 0);
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private PurchaseOrderStatus getSelectedPoStatus() {
        int row = poTable.getSelectedRow();
        if (row < 0) return null;
        Object value = tableModel.getValueAt(row, 4);
        return PurchaseOrderStatus.valueOf(value.toString());
    }

    private void onApprove() {
        handleStatusChange(PurchaseOrderStatus.APPROVED, WorkRequestStatus.COMPLETED, "Approved by supplier");
    }

    private void onReject() {
        handleStatusChange(PurchaseOrderStatus.REJECTED, WorkRequestStatus.REJECTED, "Rejected by supplier");
    }

    private void handleStatusChange(PurchaseOrderStatus poStatus,
                                    WorkRequestStatus wrStatus,
                                    String commentPrefix) {
        Integer poId = getSelectedPoId();
        if (poId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a purchase order.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderStatus current = getSelectedPoStatus();
        if (current == PurchaseOrderStatus.APPROVED || current == PurchaseOrderStatus.REJECTED) {
            JOptionPane.showMessageDialog(this,
                    "This purchase order is already " + current + ".",
                    "Invalid Operation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this,
                "Optional comment:",
                commentPrefix
        );
        if (comment == null) {
            // user cancelled
            return;
        }

        poDao.updateStatus(poId, poStatus, comment);
        wrDao.updateStatusForPurchaseOrder(poId, wrStatus, comment);

        JOptionPane.showMessageDialog(this,
                "Purchase order " + poStatus + ".",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadAllPOs();
    }
}
