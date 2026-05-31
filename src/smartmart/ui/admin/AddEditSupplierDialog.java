package smartmart.ui.admin;

import smartmart.model.Supplier;
import smartmart.service.SupplierService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddEditSupplierDialog extends JDialog {
    private final JTextField txtName;
    private final JTextField txtPhone;
    private final JTextField txtEmail;
    private final JTextField txtAddress;
    
    private final SupplierService supplierService;
    private final SupplierManagementPanel parentPanel;
    private final Supplier supplierToEdit;

    public AddEditSupplierDialog(Frame owner, SupplierManagementPanel parentPanel, Supplier supplierToEdit) {
        super(owner, supplierToEdit == null ? "Add Supplier" : "Edit Supplier", true);
        this.parentPanel = parentPanel;
        this.supplierToEdit = supplierToEdit;
        this.supplierService = new SupplierService();

        setSize(400, 320);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel lblName = new JLabel("Supplier Name:");
        lblName.setFont(UIConstants.FONT_BUTTON);
        txtName = UIHelper.createStyledTextField();

        JLabel lblPhone = new JLabel("Phone Number:");
        lblPhone.setFont(UIConstants.FONT_BUTTON);
        txtPhone = UIHelper.createStyledTextField();

        JLabel lblEmail = new JLabel("Email Address:");
        lblEmail.setFont(UIConstants.FONT_BUTTON);
        txtEmail = UIHelper.createStyledTextField();

        JLabel lblAddress = new JLabel("Office Address:");
        lblAddress.setFont(UIConstants.FONT_BUTTON);
        txtAddress = UIHelper.createStyledTextField();

        // Add components to main layout grid
        addComponent(mainPanel, lblName, gbc, 0, 0, 1);
        addComponent(mainPanel, txtName, gbc, 1, 0, 2);

        addComponent(mainPanel, lblPhone, gbc, 0, 1, 1);
        addComponent(mainPanel, txtPhone, gbc, 1, 1, 2);

        addComponent(mainPanel, lblEmail, gbc, 0, 2, 1);
        addComponent(mainPanel, txtEmail, gbc, 1, 2, 2);

        addComponent(mainPanel, lblAddress, gbc, 0, 3, 1);
        addComponent(mainPanel, txtAddress, gbc, 1, 3, 2);

        add(mainPanel, BorderLayout.CENTER);

        // Footer buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonsPanel.setBackground(new Color(245, 245, 250));
        buttonsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));

        JButton btnSave = UIHelper.createPrimaryButton("Save");
        btnSave.setPreferredSize(new Dimension(80, UIConstants.BUTTON_HEIGHT));

        JButton btnCancel = UIHelper.createSecondaryButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(80, UIConstants.BUTTON_HEIGHT));

        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnSave);
        add(buttonsPanel, BorderLayout.SOUTH);

        // Prepopulate edit inputs
        if (supplierToEdit != null) {
            txtName.setText(supplierToEdit.getName());
            txtPhone.setText(supplierToEdit.getContactPhone());
            txtEmail.setText(supplierToEdit.getEmail());
            txtAddress.setText(supplierToEdit.getAddress());
        }

        // Event Handling
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int gridx, int gridy, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        panel.add(comp, gbc);
    }

    private void handleSave() {
        String name = txtName.getText().trim();
        String phone = txtPhone.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty()) {
            UIHelper.showWarning(this, "Supplier Name cannot be empty.");
            return;
        }

        try {
            if (supplierToEdit == null) {
                // ADD mode
                Supplier newSup = new Supplier(0, name, phone, email, address);
                supplierService.addSupplier(newSup);
                UIHelper.showSuccess(this, "Supplier added successfully.");
            } else {
                // EDIT mode
                supplierToEdit.setName(name);
                supplierToEdit.setContactPhone(phone);
                supplierToEdit.setEmail(email);
                supplierToEdit.setAddress(address);
                supplierService.updateSupplier(supplierToEdit);
                UIHelper.showSuccess(this, "Supplier updated successfully.");
            }
            parentPanel.refreshTable();
            dispose();
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to save supplier: " + ex.getMessage());
        }
    }
}
