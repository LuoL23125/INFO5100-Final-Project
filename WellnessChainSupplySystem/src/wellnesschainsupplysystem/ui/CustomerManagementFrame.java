package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.CustomerDao;
import wellnesschainsupplysystem.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerManagementFrame extends JFrame {

    private final CustomerDao customerDao = new CustomerDao();

    private JTable customerTable;
    private DefaultTableModel tableModel;

    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtNotes;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

  public CustomerManagementFrame() {
    setTitle("Customer Management - Wellness Chain");
    // ❌ was EXIT_ON_CLOSE before
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
    setSize(800, 600);
    setLocationRelativeTo(null); // center on screen

    initComponents();
    loadCustomers();
}


    private void initComponents() {
        // ====== Table ======
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Phone", "Email", "Notes"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table is read-only
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // When we click a row, populate the form fields
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(customerTable);

        // ====== Form Panel ======
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Name:");
        JLabel lblPhone = new JLabel("Phone:");
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblNotes = new JLabel("Notes:");

        txtName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtNotes = new JTextField(20);

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(lblName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(lblPhone, gbc);
        gbc.gridx = 1;
        formPanel.add(txtPhone, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(lblEmail, gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        formPanel.add(lblNotes, gbc);
        gbc.gridx = 1;
        formPanel.add(txtNotes, gbc);

        // ====== Button Panel ======
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadCustomers());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        // ====== Layout main frame ======
        setLayout(new BorderLayout());

        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        List<Customer> customers = customerDao.findAll();

        tableModel.setRowCount(0); // clear existing
        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getNotes()
            });
        }

        clearForm();
    }

    private void clearForm() {
        txtName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtNotes.setText("");
        customerTable.clearSelection();
    }

    private void onTableRowSelected() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            txtName.setText((String) tableModel.getValueAt(selectedRow, 1));
            txtPhone.setText((String) tableModel.getValueAt(selectedRow, 2));
            txtEmail.setText((String) tableModel.getValueAt(selectedRow, 3));
            txtNotes.setText((String) tableModel.getValueAt(selectedRow, 4));
        }
    }

    private Integer getSelectedCustomerId() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        Object value = tableModel.getValueAt(selectedRow, 0);
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            return Integer.parseInt(value.toString());
        }
    }

    private void onAdd() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String notes = txtNotes.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer customer = new Customer(name, phone, email, notes);
        customerDao.create(customer);

        JOptionPane.showMessageDialog(this,
                "Customer added with ID: " + customer.getId(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadCustomers();
    }

    private void onUpdate() {
        Integer id = getSelectedCustomerId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer to update",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String notes = txtNotes.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer customer = new Customer(id, name, phone, email, notes);
        customerDao.update(customer);

        JOptionPane.showMessageDialog(this,
                "Customer updated",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadCustomers();
    }

    private void onDelete() {
        Integer id = getSelectedCustomerId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a customer to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            customerDao.delete(id);

            JOptionPane.showMessageDialog(this,
                    "Customer deleted",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadCustomers();
        }
    }
}
