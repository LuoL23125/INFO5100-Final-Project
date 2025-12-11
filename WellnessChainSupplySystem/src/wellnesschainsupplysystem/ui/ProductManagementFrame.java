package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.ProductDao;
import wellnesschainsupplysystem.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductManagementFrame extends JFrame {

    private final ProductDao productDao = new ProductDao();

    private JTable productTable;
    private DefaultTableModel tableModel;

    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtUnit;
    private JTextField txtUnitPrice;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    public ProductManagementFrame() {
        setTitle("Product Management - Supplier");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadProducts();
    }

    private void initComponents() {
        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Category", "Unit", "Unit Price"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane scrollPane = new JScrollPane(productTable);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Name:");
        JLabel lblCategory = new JLabel("Category:");
        JLabel lblUnit = new JLabel("Unit:");
        JLabel lblUnitPrice = new JLabel("Unit Price:");

        txtName = new JTextField(20);
        txtCategory = new JTextField(20);
        txtUnit = new JTextField(10);
        txtUnitPrice = new JTextField(10);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblCategory, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCategory, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblUnit, gbc);
        gbc.gridx = 1;
        formPanel.add(txtUnit, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblUnitPrice, gbc);
        gbc.gridx = 1;
        formPanel.add(txtUnitPrice, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadProducts());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        List<Product> products = productDao.findAll();
        tableModel.setRowCount(0);

        for (Product p : products) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    p.getUnit(),
                    p.getUnitPrice()
            });
        }

        clearForm();
    }

    private void clearForm() {
        txtName.setText("");
        txtCategory.setText("");
        txtUnit.setText("");
        txtUnitPrice.setText("");
        productTable.clearSelection();
    }

    private void onTableRowSelected() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            txtName.setText((String) tableModel.getValueAt(row, 1));
            txtCategory.setText((String) tableModel.getValueAt(row, 2));
            txtUnit.setText((String) tableModel.getValueAt(row, 3));
            txtUnitPrice.setText(tableModel.getValueAt(row, 4).toString());
        }
    }

    private Integer getSelectedProductId() {
        int row = productTable.getSelectedRow();
        if (row < 0) return null;
        Object value = tableModel.getValueAt(row, 0);
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private void onAdd() {
        String name = txtName.getText().trim();
        String category = txtCategory.getText().trim();
        String unit = txtUnit.getText().trim();
        String priceStr = txtUnitPrice.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Unit Price are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unit Price must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product p = new Product(name, category, unit, price);
        productDao.create(p);

        JOptionPane.showMessageDialog(this,
                "Product added with ID: " + p.getId(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadProducts();
    }

    private void onUpdate() {
        Integer id = getSelectedProductId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to update.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String category = txtCategory.getText().trim();
        String unit = txtUnit.getText().trim();
        String priceStr = txtUnitPrice.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name and Unit Price are required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Unit Price must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Product p = new Product(id, name, category, unit, price);
        productDao.update(p);

        JOptionPane.showMessageDialog(this,
                "Product updated.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadProducts();
    }

    private void onDelete() {
        Integer id = getSelectedProductId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            productDao.delete(id);

            JOptionPane.showMessageDialog(this,
                    "Product deleted.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadProducts();
        }
    }
}
