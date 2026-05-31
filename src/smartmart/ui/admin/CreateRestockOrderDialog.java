package smartmart.ui.admin;

import smartmart.dao.RestockOrderDAO;
import smartmart.model.Product;
import smartmart.model.RestockOrder;
import smartmart.model.Supplier;
import smartmart.service.ProductService;
import smartmart.service.SupplierService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateRestockOrderDialog extends JDialog {
    private final JComboBox<Product> comboProduct;
    private final JComboBox<Supplier> comboSupplier;
    private final JTextField txtQuantity;
    
    private final ProductService productService;
    private final SupplierService supplierService;
    private final RestockOrderDAO restockOrderDAO;
    private final RestockOrderPanel parentPanel;

    public CreateRestockOrderDialog(Frame owner, RestockOrderPanel parentPanel) {
        super(owner, "Create Restock Order", true);
        this.parentPanel = parentPanel;
        this.productService = new ProductService();
        this.supplierService = new SupplierService();
        this.restockOrderDAO = new RestockOrderDAO();

        setSize(380, 280);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblProduct = new JLabel("Product:");
        lblProduct.setFont(UIConstants.FONT_BUTTON);
        comboProduct = new JComboBox<>();
        comboProduct.setFont(UIConstants.FONT_BODY);
        comboProduct.setBackground(Color.WHITE);
        comboProduct.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Product) {
                    setText(((Product) value).getProductName());
                }
                return this;
            }
        });

        JLabel lblSupplier = new JLabel("Supplier:");
        lblSupplier.setFont(UIConstants.FONT_BUTTON);
        comboSupplier = new JComboBox<>();
        comboSupplier.setFont(UIConstants.FONT_BODY);
        comboSupplier.setBackground(Color.WHITE);
        comboSupplier.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Supplier) {
                    setText(((Supplier) value).getName());
                }
                return this;
            }
        });

        JLabel lblQuantity = new JLabel("Quantity:");
        lblQuantity.setFont(UIConstants.FONT_BUTTON);
        txtQuantity = UIHelper.createStyledTextField();

        // Populate lists
        loadData();

        // Layout Grid
        addComponent(mainPanel, lblProduct, gbc, 0, 0, 1);
        addComponent(mainPanel, comboProduct, gbc, 1, 0, 2);

        addComponent(mainPanel, lblSupplier, gbc, 0, 1, 1);
        addComponent(mainPanel, comboSupplier, gbc, 1, 1, 2);

        addComponent(mainPanel, lblQuantity, gbc, 0, 2, 1);
        addComponent(mainPanel, txtQuantity, gbc, 1, 2, 2);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons Panel
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

        // Event Handling
        
        // Auto-select supplier based on product selected
        comboProduct.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product p = (Product) comboProduct.getSelectedItem();
                if (p != null && p.getSupplier() != null) {
                    for (int i = 0; i < comboSupplier.getItemCount(); i++) {
                        Supplier s = comboSupplier.getItemAt(i);
                        if (s.getSupplierId() == p.getSupplier().getSupplierId()) {
                            comboSupplier.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
        });

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

        // Trigger action listener once to set default supplier initially
        if (comboProduct.getItemCount() > 0) {
            comboProduct.setSelectedIndex(0);
        }
    }

    private void addComponent(JPanel panel, Component comp, GridBagConstraints gbc, int gridx, int gridy, int gridwidth) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridwidth = gridwidth;
        panel.add(comp, gbc);
    }

    private void loadData() {
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            for (Supplier s : suppliers) {
                comboSupplier.addItem(s);
            }

            List<Product> products = productService.getAllProducts();
            for (Product p : products) {
                comboProduct.addItem(p);
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to load products/suppliers: " + ex.getMessage());
        }
    }

    private void handleSave() {
        Product p = (Product) comboProduct.getSelectedItem();
        Supplier s = (Supplier) comboSupplier.getSelectedItem();
        String qtyStr = txtQuantity.getText().trim();

        if (p == null || s == null || qtyStr.isEmpty()) {
            UIHelper.showWarning(this, "All fields are required.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                UIHelper.showWarning(this, "Quantity must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            UIHelper.showWarning(this, "Please enter a valid positive integer for quantity.");
            return;
        }

        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            RestockOrder order = new RestockOrder(0, p, s, quantity, "PENDING", timestamp, null);
            restockOrderDAO.createRestockOrder(order);
            
            UIHelper.showSuccess(this, "Restock order created successfully.");
            parentPanel.refreshTable();
            dispose();
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to create restock order: " + ex.getMessage());
        }
    }
}
