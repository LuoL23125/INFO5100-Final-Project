package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.ClinicBranchDao;
import wellnesschainsupplysystem.dao.ProductDao;
import wellnesschainsupplysystem.dao.PurchaseOrderDao;
import wellnesschainsupplysystem.dao.WorkRequestDao;
import wellnesschainsupplysystem.model.ClinicBranch;
import wellnesschainsupplysystem.model.Product;
import wellnesschainsupplysystem.model.PurchaseOrder;
import wellnesschainsupplysystem.model.PurchaseOrderStatus;
import wellnesschainsupplysystem.model.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PurchaseOrderManagementFrame extends JFrame {

    private final UserAccount currentUser;

    private final ClinicBranchDao branchDao = new ClinicBranchDao();
    private final ProductDao productDao = new ProductDao();
    private final PurchaseOrderDao poDao = new PurchaseOrderDao();
    private final WorkRequestDao wrDao = new WorkRequestDao();

    private JComboBox<ClinicBranch> cbBranch;
    private JComboBox<Product> cbProduct;
    private JTextField txtQuantity;

    private JTable poTable;
    private DefaultTableModel tableModel;

    private JButton btnCreate;
    private JButton btnDelete;  // NEW
    private JButton btnRefresh;

    public PurchaseOrderManagementFrame(UserAccount user) {
        this.currentUser = user;

        setTitle("Purchase Orders - Branch Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadBranches();
        loadProducts();
        loadPOsForSelectedBranch();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top: Branch selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBranch = new JLabel("Branch:");
        cbBranch = new JComboBox<>();
        cbBranch.addActionListener(e -> loadPOsForSelectedBranch());
        topPanel.add(lblBranch);
        topPanel.add(cbBranch);
        add(topPanel, BorderLayout.NORTH);

        // Center: PO table
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

        // Bottom: create new PO
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Purchase Order"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblProduct = new JLabel("Product:");
        JLabel lblQuantity = new JLabel("Quantity:");

        cbProduct = new JComboBox<>();
        txtQuantity = new JTextField(10);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblProduct, gbc);
        gbc.gridx = 1;
        formPanel.add(cbProduct, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblQuantity, gbc);
        gbc.gridx = 1;
        formPanel.add(txtQuantity, gbc);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCreate = new JButton("Create & Submit PO");
        btnDelete = new JButton("Delete PO");  // NEW
        btnRefresh = new JButton("Refresh");

        btnCreate.addActionListener(e -> onCreatePO());
        btnDelete.addActionListener(e -> onDeletePO());  // NEW
        btnRefresh.addActionListener(e -> loadPOsForSelectedBranch());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnDelete);  // NEW
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

    private void loadProducts() {
        cbProduct.removeAllItems();
        List<Product> products = productDao.findAll();
        for (Product p : products) {
            cbProduct.addItem(p);
        }
    }

    private ClinicBranch getSelectedBranch() {
        return (ClinicBranch) cbBranch.getSelectedItem();
    }

    private Product getSelectedProduct() {
        return (Product) cbProduct.getSelectedItem();
    }

    private void loadPOsForSelectedBranch() {
        ClinicBranch branch = getSelectedBranch();
        tableModel.setRowCount(0);

        if (branch == null) {
            return;
        }

        List<PurchaseOrder> pos = poDao.findByBranch(branch.getId());
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

    private void onCreatePO() {
        ClinicBranch branch = getSelectedBranch();
        Product product = getSelectedProduct();

        if (branch == null || product == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a branch and a product.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = txtQuantity.getText().trim();
        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be an integer.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Quantity must be greater than 0.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int poId = poDao.createWithSingleItem(
                branch.getId(),
                currentUser.getId(),
                product.getId(),
                qty,
                product.getUnitPrice()
        );

        if (poId <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Failed to create purchase order.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create matching work request
        wrDao.createForPurchaseOrder(poId, currentUser.getId(),
                "New purchase order submitted by branch admin");

        JOptionPane.showMessageDialog(this,
                "Purchase order created with ID: " + poId,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        txtQuantity.setText("");
        loadPOsForSelectedBranch();
    }

    // NEW: Delete Purchase Order
    private void onDeletePO() {
        Integer poId = getSelectedPoId();
        if (poId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a purchase order to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        PurchaseOrderStatus status = getSelectedPoStatus();
        if (status != PurchaseOrderStatus.SUBMITTED) {
            JOptionPane.showMessageDialog(this,
                    "Only SUBMITTED orders can be deleted.\n" +
                    "This order is already " + status + ".",
                    "Cannot Delete",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Purchase Order #" + poId + "?\n" +
                "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            boolean deleted = poDao.delete(poId);
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                        "Purchase order deleted.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadPOsForSelectedBranch();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete purchase order.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}