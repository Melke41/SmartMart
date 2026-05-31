package smartmart.ui.cashier;

import smartmart.exception.SmartMartException;
import smartmart.model.Sale;
import smartmart.model.SaleItem;
import smartmart.service.AuthService;
import smartmart.service.SaleService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionHistoryPanel extends JPanel {

    private final SaleService saleService;
    private final AuthService authService;

    private JTable salesTable;
    private DefaultTableModel salesTableModel;
    private JTable itemsTable;
    private DefaultTableModel itemsTableModel;

    private JTextField dateField;
    private JLabel summaryLabel;
    private JLabel detailTitleLabel;

    private List<Sale> currentSales;

    public TransactionHistoryPanel() {
        saleService = new SaleService();
        authService = new AuthService();

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initUI();
        loadTodaySales();
    }

    private void initUI() {
        // Top Toolbar
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(Color.WHITE);

        topPanel.add(UIHelper.createTitleLabel("My Transaction History"));

        topPanel.add(new JLabel("  Date:"));
        dateField = UIHelper.createStyledTextField();
        dateField.setPreferredSize(new Dimension(130, UIConstants.FORM_FIELD_HEIGHT));
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        topPanel.add(dateField);

        JButton filterBtn = UIHelper.createPrimaryButton("Filter");
        JButton todayBtn = UIHelper.createSecondaryButton("Today");

        topPanel.add(filterBtn);
        topPanel.add(todayBtn);

        add(topPanel, BorderLayout.NORTH);

        // Center SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(450); // 40-60 roughly
        splitPane.setDividerSize(5);

        // Left side: Sales Master
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        String[] salesColumns = {"Sale ID", "Time", "Items", "Total (ETB)"};
        salesTableModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        salesTable = UIHelper.createStyledTable();
        salesTable.setModel(salesTableModel);
        
        JScrollPane salesScroll = new JScrollPane(salesTable);
        salesScroll.getViewport().setBackground(Color.WHITE);
        leftPanel.add(salesScroll, BorderLayout.CENTER);

        // Right side: Detail
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

        detailTitleLabel = UIHelper.createSubtitleLabel("Select a sale to view items");
        rightPanel.add(detailTitleLabel, BorderLayout.NORTH);

        String[] itemColumns = {"Product", "Qty", "Unit Price", "Subtotal"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        itemsTable = UIHelper.createStyledTable();
        itemsTable.setModel(itemsTableModel);
        
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.getViewport().setBackground(Color.WHITE);
        rightPanel.add(itemsScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        // Bottom Summary
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        summaryLabel = new JLabel("Total sales today: 0 | Total revenue: ETB 0.00");
        summaryLabel.setFont(UIConstants.FONT_BODY.deriveFont(Font.BOLD));
        bottomPanel.add(summaryLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        // Listeners
        filterBtn.addActionListener(e -> loadSalesByDate(dateField.getText()));
        todayBtn.addActionListener(e -> {
            dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            loadTodaySales();
        });

        salesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && salesTable.getSelectedRow() != -1) {
                int saleId = (int) salesTableModel.getValueAt(salesTable.getSelectedRow(), 0);
                showSaleItems(saleId);
            }
        });
    }

    private void loadTodaySales() {
        SwingWorker<List<Sale>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Sale> doInBackground() throws Exception {
                // To filter today's sales for THIS cashier specifically
                List<Sale> allMySales = saleService.getSalesByCashier(authService.getCurrentUser().getUserId());
                String todayStr = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                
                // Filter memory to just today
                allMySales.removeIf(s -> !s.getSaleDate().startsWith(todayStr));
                return allMySales;
            }

            @Override
            protected void done() {
                try {
                    currentSales = get();
                    refreshSalesTable();
                } catch (Exception e) {
                    UIHelper.showError(TransactionHistoryPanel.this, "Failed to load sales: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void loadSalesByDate(String dateStr) {
        SwingWorker<List<Sale>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Sale> doInBackground() throws Exception {
                List<Sale> dateSales = saleService.getSalesByDate(dateStr);
                // Filter by this cashier
                int myId = authService.getCurrentUser().getUserId();
                dateSales.removeIf(s -> s.getCashier() == null || s.getCashier().getUserId() != myId);
                return dateSales;
            }

            @Override
            protected void done() {
                try {
                    currentSales = get();
                    refreshSalesTable();
                } catch (Exception e) {
                    UIHelper.showError(TransactionHistoryPanel.this, "Failed to load sales: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void refreshSalesTable() {
        salesTableModel.setRowCount(0);
        itemsTableModel.setRowCount(0);
        detailTitleLabel.setText("Select a sale to view items");
        
        double totalRevenue = 0;
        
        if (currentSales != null) {
            for (Sale s : currentSales) {
                salesTableModel.addRow(new Object[]{
                    s.getSaleId(),
                    s.getSaleDate(),
                    s.getItems().size(),
                    String.format("%.2f", s.getTotalAmount())
                });
                totalRevenue += s.getTotalAmount();
            }
        }
        
        summaryLabel.setText(String.format("Total sales: %d | Total revenue: ETB %.2f", 
            currentSales != null ? currentSales.size() : 0, totalRevenue));
    }

    private void showSaleItems(int saleId) {
        itemsTableModel.setRowCount(0);
        Sale selectedSale = null;
        for (Sale s : currentSales) {
            if (s.getSaleId() == saleId) {
                selectedSale = s;
                break;
            }
        }
        
        if (selectedSale != null) {
            detailTitleLabel.setText(String.format("Sale REC-%04d | Total: ETB %.2f", selectedSale.getSaleId(), selectedSale.getTotalAmount()));
            for (SaleItem item : selectedSale.getItems()) {
                itemsTableModel.addRow(new Object[]{
                    item.getProduct().getProductName(),
                    item.getQuantity(),
                    String.format("%.2f", item.getUnitPrice()),
                    String.format("%.2f", item.getSubtotal())
                });
            }
        }
    }
}
