package wellnesschainsupplysystem.ui;

import wellnesschainsupplysystem.dao.ClinicBranchDao;
import wellnesschainsupplysystem.model.ClinicBranch;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BranchManagementFrame extends JFrame {

    private final ClinicBranchDao branchDao = new ClinicBranchDao();

    private JTable branchTable;
    private DefaultTableModel tableModel;

    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtCity;
    private JTextField txtOpeningTime;
    private JTextField txtClosingTime;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    public BranchManagementFrame() {
        setTitle("Clinic Branch Management - Wellness Chain");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // don't exit whole app
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        loadBranches();
    }

    private void initComponents() {
        // Table
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Address", "City", "Opening", "Closing"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        branchTable = new JTable(tableModel);
        branchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        branchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(branchTable);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Branch Details"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblName = new JLabel("Name:");
        JLabel lblAddress = new JLabel("Address:");
        JLabel lblCity = new JLabel("City:");
        JLabel lblOpening = new JLabel("Opening Time (HH:MM):");
        JLabel lblClosing = new JLabel("Closing Time (HH:MM):");

        txtName = new JTextField(20);
        txtAddress = new JTextField(20);
        txtCity = new JTextField(20);
        txtOpeningTime = new JTextField(10);
        txtClosingTime = new JTextField(10);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblName, gbc);
        gbc.gridx = 1;
        formPanel.add(txtName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblAddress, gbc);
        gbc.gridx = 1;
        formPanel.add(txtAddress, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblCity, gbc);
        gbc.gridx = 1;
        formPanel.add(txtCity, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblOpening, gbc);
        gbc.gridx = 1;
        formPanel.add(txtOpeningTime, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(lblClosing, gbc);
        gbc.gridx = 1;
        formPanel.add(txtClosingTime, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnRefresh.addActionListener(e -> loadBranches());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnRefresh);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadBranches() {
        List<ClinicBranch> branches = branchDao.findAll();
        tableModel.setRowCount(0);

        for (ClinicBranch b : branches) {
            tableModel.addRow(new Object[]{
                    b.getId(),
                    b.getName(),
                    b.getAddress(),
                    b.getCity(),
                    b.getOpeningTime(),
                    b.getClosingTime()
            });
        }

        clearForm();
    }

    private void clearForm() {
        txtName.setText("");
        txtAddress.setText("");
        txtCity.setText("");
        txtOpeningTime.setText("");
        txtClosingTime.setText("");
        branchTable.clearSelection();
    }

    private void onTableRowSelected() {
        int row = branchTable.getSelectedRow();
        if (row >= 0) {
            txtName.setText((String) tableModel.getValueAt(row, 1));
            txtAddress.setText((String) tableModel.getValueAt(row, 2));
            txtCity.setText((String) tableModel.getValueAt(row, 3));
            txtOpeningTime.setText((String) tableModel.getValueAt(row, 4));
            txtClosingTime.setText((String) tableModel.getValueAt(row, 5));
        }
    }

    private Integer getSelectedBranchId() {
        int row = branchTable.getSelectedRow();
        if (row < 0) {
            return null;
        }
        Object value = tableModel.getValueAt(row, 0);
        if (value instanceof Integer) return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private void onAdd() {
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String city = txtCity.getText().trim();
        String opening = txtOpeningTime.getText().trim();
        String closing = txtClosingTime.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClinicBranch b = new ClinicBranch(name, address, city, opening, closing);
        branchDao.create(b);

        JOptionPane.showMessageDialog(this,
                "Branch added with ID: " + b.getId(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadBranches();
    }

    private void onUpdate() {
        Integer id = getSelectedBranchId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a branch to update",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String city = txtCity.getText().trim();
        String opening = txtOpeningTime.getText().trim();
        String closing = txtClosingTime.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Name is required",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ClinicBranch b = new ClinicBranch(id, name, address, city, opening, closing);
        branchDao.update(b);

        JOptionPane.showMessageDialog(this,
                "Branch updated",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        loadBranches();
    }

    private void onDelete() {
        Integer id = getSelectedBranchId();
        if (id == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a branch to delete",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this branch?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            branchDao.delete(id);

            JOptionPane.showMessageDialog(this,
                    "Branch deleted",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            loadBranches();
        }
    }
}
