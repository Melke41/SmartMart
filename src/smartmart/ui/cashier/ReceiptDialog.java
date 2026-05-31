package smartmart.ui.cashier;

import smartmart.model.Sale;
import smartmart.model.SaleItem;
import smartmart.service.ReportService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReceiptDialog extends JDialog {

    private final Sale sale;
    private final double subtotal;
    private final double tax;
    private final double total;
    private final ReportService reportService;
    private JTextArea receiptArea;

    public ReceiptDialog(Window owner, Sale sale, double subtotal, double tax, double total) {
        super(owner, "Receipt", Dialog.ModalityType.APPLICATION_MODAL);
        this.sale = sale;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.reportService = new ReportService();

        initUI();
    }

    private void initUI() {
        setSize(420, 520);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(20, 20));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel storeName = new JLabel("SmartMart Retail Store", SwingConstants.CENTER);
        storeName.setFont(UIConstants.FONT_TITLE.deriveFont(Font.BOLD, 20f));
        headerPanel.add(storeName);

        JLabel subtitle = UIHelper.createSubtitleLabel("Official Receipt");
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(subtitle);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Receipt Content
        receiptArea = new JTextArea();
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Courier New", Font.PLAIN, 13));
        receiptArea.setBackground(new Color(250, 250, 250));
        receiptArea.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 225)));
        
        generateReceiptText();

        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton saveBtn = UIHelper.createSecondaryButton("Print Receipt");
        JButton newSaleBtn = UIHelper.createPrimaryButton("New Sale");

        saveBtn.addActionListener(e -> saveReceiptToFile());
        newSaleBtn.addActionListener(e -> dispose());

        btnPanel.add(saveBtn);
        btnPanel.add(newSaleBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Auto-select text on open
        SwingUtilities.invokeLater(() -> {
            receiptArea.selectAll();
            receiptArea.requestFocusInWindow();
        });
    }

    private void generateReceiptText() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("================================\n");
        sb.append(String.format("Receipt No: REC-%04d\n", sale.getSaleId()));
        sb.append("Date & Time: ").append(sale.getSaleDate()).append("\n");
        sb.append("Cashier: ").append(sale.getCashier().getFullName()).append("\n");
        sb.append("================================\n");
        
        for (SaleItem item : sale.getItems()) {
            String name = item.getProduct().getProductName();
            if (name.length() > 15) name = name.substring(0, 12) + "...";
            sb.append(String.format("%-15s %3d x %-6.2f %6.2f\n", 
                name, item.getQuantity(), item.getUnitPrice(), item.getSubtotal()));
        }
        
        sb.append("================================\n");
        sb.append(String.format("%-24s %7.2f\n", "Subtotal:", subtotal));
        sb.append(String.format("%-24s %7.2f\n", "Tax (15%):", tax));
        sb.append(String.format("%-24s %7.2f\n", "TOTAL:", total));
        sb.append("================================\n");
        sb.append("  Thank you for shopping at     \n");
        sb.append("          SmartMart!            \n");

        receiptArea.setText(sb.toString());
    }

    private void saveReceiptToFile() {
        String fileName = String.format("receipt_REC-%04d.txt", sale.getSaleId());
        boolean success = reportService.saveReportToFile(receiptArea.getText(), fileName);
        if (success) {
            UIHelper.showSuccess(this, "Receipt saved to docs/" + fileName);
        } else {
            UIHelper.showError(this, "Failed to save receipt.");
        }
    }
}
