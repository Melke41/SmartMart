package smartmart.ui.admin;

import smartmart.dao.CategoryDAO;
import smartmart.model.Category;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private final JList<Category> categoryList;
    private final DefaultListModel<Category> listModel;
    private final CategoryDAO categoryDAO;

    public CategoryManagementPanel() {
        this.categoryDAO = new CategoryDAO();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Top Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolbarPanel.setOpaque(false);

        JLabel lblTitle = UIHelper.createSubtitleLabel("Category Management");
        lblTitle.setFont(UIConstants.FONT_TITLE);

        JButton btnAdd = UIHelper.createSuccessButton("Add Category");
        btnAdd.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));

        JButton btnDelete = UIHelper.createDangerButton("Delete Category");
        btnDelete.setPreferredSize(new Dimension(140, UIConstants.FORM_FIELD_HEIGHT));

        toolbarPanel.add(lblTitle);
        toolbarPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnDelete);
        add(toolbarPanel, BorderLayout.NORTH);

        // Center List Setup
        listModel = new DefaultListModel<>();
        categoryList = new JList<>(listModel);
        categoryList.setFont(UIConstants.FONT_BODY);
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryList.setFixedCellHeight(35);
        categoryList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText("  " + ((Category) value).getCategoryName());
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(categoryList);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 225)));
        add(scrollPane, BorderLayout.CENTER);

        // Event Handling
        
        // Add Category
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryName = JOptionPane.showInputDialog(
                        CategoryManagementPanel.this,
                        "Enter new category name:",
                        "Add Category",
                        JOptionPane.PLAIN_MESSAGE
                );

                if (categoryName != null) {
                    categoryName = categoryName.trim();
                    if (categoryName.isEmpty()) {
                        UIHelper.showWarning(CategoryManagementPanel.this, "Category name cannot be empty.");
                        return;
                    }

                    try {
                        Category newCat = new Category(0, categoryName);
                        categoryDAO.addCategory(newCat);
                        UIHelper.showSuccess(CategoryManagementPanel.this, "Category added successfully.");
                        refreshList();
                    } catch (Exception ex) {
                        UIHelper.showError(CategoryManagementPanel.this, "Failed to add category: " + ex.getMessage());
                    }
                }
            }
        });

        // Delete Category
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Category selectedCat = categoryList.getSelectedValue();
                if (selectedCat == null) {
                    UIHelper.showWarning(CategoryManagementPanel.this, "Please select a category to delete.");
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(
                        CategoryManagementPanel.this,
                        "Are you sure you want to delete category '" + selectedCat.getCategoryName() + "'?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        categoryDAO.deleteCategory(selectedCat.getCategoryId());
                        UIHelper.showSuccess(CategoryManagementPanel.this, "Category deleted successfully.");
                        refreshList();
                    } catch (Exception ex) {
                        UIHelper.showError(CategoryManagementPanel.this, "Failed to delete category: " + ex.getMessage() + "\n(Verify no products are using it)");
                    }
                }
            }
        });

        // Initial Load
        refreshList();
    }

    private void refreshList() {
        listModel.clear();
        SwingWorker<List<Category>, Void> worker = new SwingWorker<List<Category>, Void>() {
            @Override
            protected List<Category> doInBackground() throws Exception {
                return categoryDAO.getAllCategories();
            }

            @Override
            protected void done() {
                try {
                    List<Category> categories = get();
                    for (Category c : categories) {
                        listModel.addElement(c);
                    }
                } catch (Exception ex) {
                    UIHelper.showError(CategoryManagementPanel.this, "Error loading categories: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}
