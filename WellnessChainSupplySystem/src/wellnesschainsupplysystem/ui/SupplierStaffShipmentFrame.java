package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.BranchInventoryDao;
import wellnesschainsupplysystem.dao.PurchaseOrderDao;
import wellnesschainsupplysystem.dao.PurchaseOrderDao.PoItemInfo;
import wellnesschainsupplysystem.dao.WorkRequestDao;
import wellnesschainsupplysystem.model.PurchaseOrder;
import wellnesschainsupplysystem.model.PurchaseOrderStatus;
import wellnesschainsupplysystem.model.UserAccount;
import wellnesschainsupplysystem.model.WorkRequestStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierStaffShipmentFrame extends JFrame {

    private final UserAccount currentUser;

    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final WorkRequestDao wrDao = new WorkRequestDao();
    private final BranchInventoryDao branchInventoryDao = new BranchInventoryDao();

    private JTable poTable;
    private DefaultTableModel tableModel;

    private JButton btnPacked;
    private JButton btnShipped;
    private JButton btnDelivered;
    private JButton btnRefresh;

    public SupplierStaffShipmentFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Shipments - Supplier Staff");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadRelevantPOs();
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
        btnPacked = new JButton("Mark Packed");
        btnShipped = new JButton("Mark Shipped");
        btnDelivered = new JButton("Mark Delivered");
        btnRefresh = new JButton("Refresh");

        btnPacked.addActionListener(e -> onMarkPacked());
        btnShipped.addActionListener(e -> onMarkShipped());
        btnDelivered.addActionListener(e -> onMarkDelivered());
        btnRefresh.addActionListener(e -> loadRelevantPOs());

        buttonPanel.add(btnPacked);
        buttonPanel.add(btnShipped);
        buttonPanel.add(btnDelivered);
        buttonPanel.add(btnRefresh);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRelevantPOs() {
        tableModel.setRowCount(0);
        List<PurchaseOrder> pos = poDao.findAllWithSummary();

        for (PurchaseOrder po : pos) {
            PurchaseOrderStatus st = po.getStatus();
            // Only show POs that need shipment handling
            if (st == PurchaseOrderStatus.APPROVED ||
                st == PurchaseOrderStatus.PACKED ||
                st == PurchaseOrderStatus.SHIPPED ||
                st == PurchaseOrderStatus.DELIVERED) {

                tableModel.addRow(new Object[]{
                        po.getId(),
                        po.getBranchName(),
                        po.getProductName(),
                        po.getQuantity(),
                        st
                });
            }
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

    private int getBranchIdForPo(int poId) {
        List<PurchaseOrder> all = poDao.findAllWithSummary();
        for (PurchaseOrder po : all) {
            if (po.getId() == poId) {
                return po.getBranchId();
            }
        }
        return -1;
    }

    private void onMarkPacked() {
        Integer poId = getSelectedPoId();
        if (poId == null) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderStatus current = getSelectedPoStatus();
        if (current != PurchaseOrderStatus.APPROVED) {
            JOptionPane.showMessageDialog(this,
                    "Only APPROVED orders can be marked PACKED.",
                    "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this, "Optional comment:", "Packed by warehouse staff");
        if (comment == null) return;

        poDao.updateStatus(poId, PurchaseOrderStatus.PACKED, comment);
        wrDao.createShipmentForPurchaseOrder(poId, currentUser.getId(),
                "Shipment started (PACKED). " + comment);

        JOptionPane.showMessageDialog(this,
                "Order marked PACKED.", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadRelevantPOs();
    }

    private void onMarkShipped() {
        Integer poId = getSelectedPoId();
        if (poId == null) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderStatus current = getSelectedPoStatus();
        if (current != PurchaseOrderStatus.PACKED) {
            JOptionPane.showMessageDialog(this,
                    "Only PACKED orders can be marked SHIPPED.",
                    "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this, "Optional comment:", "Shipped from warehouse");
        if (comment == null) return;

        poDao.updateStatus(poId, PurchaseOrderStatus.SHIPPED, comment);
        wrDao.updateShipmentStatusForPurchaseOrder(poId,
                WorkRequestStatus.IN_PROGRESS,
                "Shipment status: SHIPPED. " + comment);

        JOptionPane.showMessageDialog(this,
                "Order marked SHIPPED.", "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadRelevantPOs();
    }

    private void onMarkDelivered() {
        Integer poId = getSelectedPoId();
        if (poId == null) {
            JOptionPane.showMessageDialog(this, "Please select a purchase order.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderStatus current = getSelectedPoStatus();
        if (current != PurchaseOrderStatus.SHIPPED) {
            JOptionPane.showMessageDialog(this,
                    "Only SHIPPED orders can be marked DELIVERED.",
                    "Invalid Operation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String comment = JOptionPane.showInputDialog(
                this, "Optional comment:", "Delivered to branch");
        if (comment == null) return;

        // Update inventory
        PoItemInfo itemInfo = poDao.getSingleItemForPo(poId);
        if (itemInfo == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not load purchase order item.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int branchId = getBranchIdForPo(poId);
        if (branchId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Could not determine branch for this order.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        branchInventoryDao.addStockForShipment(
                branchId,
                itemInfo.getProductId(),
                itemInfo.getQuantity(),
                5  // default threshold
        );

        // Update PO + work request
        poDao.updateStatus(poId, PurchaseOrderStatus.DELIVERED, comment);
        wrDao.updateShipmentStatusForPurchaseOrder(poId,
                WorkRequestStatus.COMPLETED,
                "Shipment status: DELIVERED, inventory updated. " + comment);

        JOptionPane.showMessageDialog(this,
                "Order marked DELIVERED and inventory updated.",
                "Success", JOptionPane.INFORMATION_MESSAGE);

        loadRelevantPOs();
    }
}
