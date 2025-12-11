package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.BranchInventoryDao;
import wellnesschainsupplysystem.dao.ClinicBranchDao;
import wellnesschainsupplysystem.dao.ProductDao;
import wellnesschainsupplysystem.model.BranchInventory;
import wellnesschainsupplysystem.model.ClinicBranch;
import wellnesschainsupplysystem.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchInventoryFrame extends JFrame {

    private final ClinicBranchDao branchDao = new ClinicBranchDao();
    private final ProductDao productDao = new ProductDao();
    private final BranchInventoryDao inventoryDao = new BranchInventoryDao();

    private JComboBox<ClinicBranch> cbBranch;
    private JComboBox<Product> cbProduct;

    private JTable inventoryTable;
    private DefaultTableModel tableModel;

    private JTextField txtQuantity;
    private JTextField txtThreshold;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    // cache for product names by id
    private Map<Integer, Product> productMap = new HashMap<>();

    public BranchInventoryFrame() {
        setTitle("Branch Inventory Management - Wellness Chain");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadBranches();
        loadProducts();
        loadInventoryForSelectedBranch();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // ===== Top Panel: branch selector =====
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblBranch = new JLabel("Branch:");
        cbBranch = new JComboBox<>();
        cbBranch.addActionListener(e -> loadInventoryForSelectedBranch());

        topPanel.add(lblBranch);
        topPanel.add(cbBranch);

        add(topPanel, BorderLayout.NORTH);

        // ===== Center: inventory table =====
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Product", "Quantity", "Reorder Threshold"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // ===== Form + buttons =====
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblProduct = new JLabel("Product:");
        JLabel lblQuantity = new JLabel("Quantity On Hand:");
        JLabel lblThreshold = new JLabel("Reorder Threshold:");

        cbProduct = new JComboBox<>();
        txtQuantity = new JTextField(10);
        txtThreshold = new JTextField(10);

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

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblThreshold, gbc);
        gbc.gridx = 1;
        formPanel.add(txtThreshold, gbc);

        bottomPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add/Set");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadInventoryForSelectedBranch());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
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
        productMap.clear();

        List<Product> products = productDao.findAll();
        for (Product p : products) {
            cbProduct.addItem(p);
            productMap.put(p.getId(), p);
        }
    }

    private ClinicBranch getSelectedBranch() {
        return (ClinicBranch) cbBranch.getSelectedItem();
    }

    private Product getSelectedProduct() {
        return (Product) cbProduct.getSelectedItem();
    }

    private void loadInventoryForSelectedBranch() {
        ClinicBranch branch = getSelectedBranch();
        if (branch == null) {
            tableModel.setRowCount(0);
            return;
        }

        List<BranchInventory> inventory = inventoryDao.findByBranchId(branch.getId());
        tableModel.setRowCount(0);

        for (BranchInventory inv : inventory) {
            Product p = productMap.get(inv.getProductId());
            String productName = (p != null) ? p.getName() : ("Product ID " + inv.getProductId());

            tableModel.addRow(new Object[]{
                    inv.getId(),
                    productName,
                    inv.getQuantityOnHand(),
                    inv.getReorderThreshold()
            });
        }

        clearForm();
    }

    private void clearForm() {
        txtQuantity.setText("");
        txtThreshold.setText("");
        inventoryTable.clearSelection();
    }

    private void onTableRowSelected() {
        int row = inventoryTable.getSelectedRow();
        if (row >= 0) {
            String productName = (String) tableModel.getValueAt(row, 1);
            int quantity = (int) tableModel.getValueAt(row, 2);
            int threshold = (int) tableModel.getValueAt(row, 3);

            // select product in combo box by name (simple match)
            for (int i = 0; i < cbProduct.getItemCount(); i++) {
                Product p = cbProduct.getItemAt(i);
                if (p.getName().equals(productName)) {
                    cbProduct.setSelectedIndex(i);
                    break;
                }
            }

            txtQuantity.setText(String.valueOf(quantity));
            txtThreshold.setText(String.valueOf(threshold));
        }
    }

    private Integer getSelectedInventoryId() {
        int row = inventoryTable.getSelectedRow();
        if (row < 0) return null;
        Object value = tableModel.getValueAt(row, 0);
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private void onAdd() {
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
        String thStr = txtThreshold.getText().trim();

        int qty, threshold;
        try {
            qty = Integer.parseInt(qtyStr);
            threshold = Integer.parseInt(thStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity and Reorder Threshold must be integers.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BranchInventory inv = new BranchInventory(
                branch.getId(),
                product.getId(),
                qty,
                threshold
        );
        inventoryDao.create(inv);

        JOptionPane.showMessageDialog(this,
                "Inventory entry created.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadInventoryForSelectedBranch();
    }

    private void onUpdate() {
        Integer invId = getSelectedInventoryId();
        if (invId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an inventory row to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String qtyStr = txtQuantity.getText().trim();
        String thStr = txtThreshold.getText().trim();

        int qty, threshold;
        try {
            qty = Integer.parseInt(qtyStr);
            threshold = Integer.parseInt(thStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Quantity and Reorder Threshold must be integers.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BranchInventory inv = new BranchInventory();
        inv.setId(invId);
        inv.setQuantityOnHand(qty);
        inv.setReorderThreshold(threshold);

        inventoryDao.update(inv);

        JOptionPane.showMessageDialog(this,
                "Inventory updated.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadInventoryForSelectedBranch();
    }

    private void onDelete() {
        Integer invId = getSelectedInventoryId();
        if (invId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an inventory row to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this inventory entry?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            inventoryDao.delete(invId);

            JOptionPane.showMessageDialog(this,
                    "Inventory entry deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadInventoryForSelectedBranch();
        }
    }
}
