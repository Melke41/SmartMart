package smartmart.ui.admin;

import smartmart.dao.CategoryDAO;
import smartmart.exception.InvalidProductException;
import smartmart.model.Category;
import smartmart.model.Product;
import smartmart.model.Supplier;
import smartmart.service.ProductService;
import smartmart.service.SupplierService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddEditProductDialog extends JDialog {
    private final JTextField txtName;
    private final JComboBox<Category> comboCategory;
    private final JComboBox<Supplier> comboSupplier;
    private final JTextField txtPrice;
    private final JTextField txtStock;
    private final JTextField txtLimit;
    
    private final ProductService productService;
    private final ProductManagementPanel parentPanel;
    private final Product productToEdit; // null in ADD mode

    public AddEditProductDialog(Frame owner, ProductManagementPanel parentPanel, Product productToEdit) {
        super(owner, productToEdit == null ? "Add Product" : "Edit Product", true);
        this.parentPanel = parentPanel;
        this.productToEdit = productToEdit;
        this.productService = new ProductService();

        setSize(450, 430);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Labels and Fields
        JLabel lblName = new JLabel("Product Name:");
        lblName.setFont(UIConstants.FONT_BUTTON);
        txtName = UIHelper.createStyledTextField();

        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setFont(UIConstants.FONT_BUTTON);
        comboCategory = new JComboBox<>();
        comboCategory.setFont(UIConstants.FONT_BODY);
        comboCategory.setBackground(Color.WHITE);
        comboCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getCategoryName());
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

        JLabel lblPrice = new JLabel("Price (ETB):");
        lblPrice.setFont(UIConstants.FONT_BUTTON);
        txtPrice = UIHelper.createStyledTextField();

        JLabel lblStock = new JLabel("Stock Quantity:");
        lblStock.setFont(UIConstants.FONT_BUTTON);
        txtStock = UIHelper.createStyledTextField();

        JLabel lblLimit = new JLabel("Low Stock Threshold:");
        lblLimit.setFont(UIConstants.FONT_BUTTON);
        txtLimit = UIHelper.createStyledTextField();

        // Populate ComboBoxes
        loadComboBoxData();

        // Layout components
        addComponent(mainPanel, lblName, gbc, 0, 0, 1);
        addComponent(mainPanel, txtName, gbc, 1, 0, 2);

        addComponent(mainPanel, lblCategory, gbc, 0, 1, 1);
        addComponent(mainPanel, comboCategory, gbc, 1, 1, 2);

        addComponent(mainPanel, lblSupplier, gbc, 0, 2, 1);
        addComponent(mainPanel, comboSupplier, gbc, 1, 2, 2);

        addComponent(mainPanel, lblPrice, gbc, 0, 3, 1);
        addComponent(mainPanel, txtPrice, gbc, 1, 3, 2);

        addComponent(mainPanel, lblStock, gbc, 0, 4, 1);
        addComponent(mainPanel, txtStock, gbc, 1, 4, 2);

        addComponent(mainPanel, lblLimit, gbc, 0, 5, 1);
        addComponent(mainPanel, txtLimit, gbc, 1, 5, 2);

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

        // Populate if in Edit Mode
        if (productToEdit != null) {
            txtName.setText(productToEdit.getProductName());
            txtPrice.setText(String.valueOf(productToEdit.getPrice()));
            txtStock.setText(String.valueOf(productToEdit.getStockQty()));
            txtLimit.setText(String.valueOf(productToEdit.getLowStockLimit()));

            // Select matching category and supplier
            for (int i = 0; i < comboCategory.getItemCount(); i++) {
                Category c = comboCategory.getItemAt(i);
                if (productToEdit.getCategory() != null && c.getCategoryId() == productToEdit.getCategory().getCategoryId()) {
                    comboCategory.setSelectedIndex(i);
                    break;
                }
            }

            for (int i = 0; i < comboSupplier.getItemCount(); i++) {
                Supplier s = comboSupplier.getItemAt(i);
                if (productToEdit.getSupplier() != null && s.getSupplierId() == productToEdit.getSupplier().getSupplierId()) {
                    comboSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Action Listeners
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

    private void loadComboBoxData() {
        try {
            List<Category> categories = new CategoryDAO().getAllCategories();
            for (Category c : categories) {
                comboCategory.addItem(c);
            }

            List<Supplier> suppliers = new SupplierService().getAllSuppliers();
            for (Supplier s : suppliers) {
                comboSupplier.addItem(s);
            }
        } catch (Exception ex) {
            UIHelper.showError(this, "Error loading categories/suppliers: " + ex.getMessage());
        }
    }

    private void handleSave() {
        String name = txtName.getText().trim();
        Category category = (Category) comboCategory.getSelectedItem();
        Supplier supplier = (Supplier) comboSupplier.getSelectedItem();
        String priceStr = txtPrice.getText().trim();
        String stockStr = txtStock.getText().trim();
        String limitStr = txtLimit.getText().trim();

        if (name.isEmpty() || category == null || supplier == null || priceStr.isEmpty() || stockStr.isEmpty() || limitStr.isEmpty()) {
            UIHelper.showWarning(this, "All fields are required.");
            return;
        }

        double price;
        int stock;
        int limit;

        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                UIHelper.showWarning(this, "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            UIHelper.showWarning(this, "Please enter a valid decimal number for price.");
            return;
        }

        try {
            stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                UIHelper.showWarning(this, "Stock quantity cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            UIHelper.showWarning(this, "Please enter a valid integer for stock quantity.");
            return;
        }

        try {
            limit = Integer.parseInt(limitStr);
            if (limit < 0) {
                UIHelper.showWarning(this, "Low stock threshold limit cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            UIHelper.showWarning(this, "Please enter a valid integer for low stock limit.");
            return;
        }

        try {
            if (productToEdit == null) {
                // ADD mode
                Product newProduct = new Product(0, name, category, supplier, price, stock, limit);
                productService.addProduct(newProduct);
                UIHelper.showSuccess(this, "Product added successfully.");
            } else {
                // EDIT mode
                productToEdit.setProductName(name);
                productToEdit.setCategory(category);
                productToEdit.setSupplier(supplier);
                productToEdit.setPrice(price);
                productToEdit.setStockQty(stock);
                productToEdit.setLowStockLimit(limit);
                productService.updateProduct(productToEdit);
                UIHelper.showSuccess(this, "Product updated successfully.");
            }
            parentPanel.refreshTable();
            dispose();
        } catch (InvalidProductException ex) {
            UIHelper.showError(this, ex.getMessage());
        } catch (Exception ex) {
            UIHelper.showError(this, "Failed to save product: " + ex.getMessage());
        }
    }
}
