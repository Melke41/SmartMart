package smartmart.ui;

import smartmart.model.User;
import smartmart.service.AuthService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class BaseDashboardFrame extends JFrame {
    protected final User currentUser;
    protected final JPanel contentPanel;
    protected final JLabel statusLabel;
    protected final JPanel sidebarPanel;
    
    private final JLabel lblPageTitle;
    private final CardLayout cardLayout;

    public BaseDashboardFrame(User user) {
        super(user.getDashboardTitle() + " — SmartMart");
        this.currentUser = user;

        // Frame configuration
        setSize(1200, 720);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. Top Bar (Header) - BACKGROUND_COLOR, height 55px
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        headerPanel.setPreferredSize(new Dimension(1200, 55));
        headerPanel.setBorder(new MatteBorder(0, 0, 1, 0, new Color(220, 220, 225)));

        // Header Left: Store name / Logo
        JLabel lblLogo = new JLabel("  " + UIConstants.STORE_NAME);
        lblLogo.setFont(UIConstants.FONT_SUBTITLE);
        lblLogo.setForeground(UIConstants.PRIMARY_COLOR);
        lblLogo.setVerticalAlignment(JLabel.CENTER);
        headerPanel.add(lblLogo, BorderLayout.WEST);

        // Header Center: Dynamic Page Title
        lblPageTitle = new JLabel("Dashboard", SwingConstants.CENTER);
        lblPageTitle.setFont(UIConstants.FONT_SUBTITLE);
        lblPageTitle.setForeground(UIConstants.TEXT_PRIMARY);
        headerPanel.add(lblPageTitle, BorderLayout.CENTER);

        // Header Right: User Info + Logout Button
        JPanel headerRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        headerRightPanel.setOpaque(false);

        JLabel lblUserInfo = new JLabel(currentUser.getFullName() + " | " + currentUser.getRole().getDisplayName());
        lblUserInfo.setFont(UIConstants.FONT_BODY);
        lblUserInfo.setForeground(UIConstants.TEXT_PRIMARY);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(UIConstants.DANGER_COLOR);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setOpaque(true);
        btnLogout.setContentAreaFilled(true);
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setPreferredSize(new Dimension(90, 30));
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AuthService().logout();
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        headerRightPanel.add(lblUserInfo);
        headerRightPanel.add(btnLogout);
        headerPanel.add(headerRightPanel, BorderLayout.EAST);

        // 2. Left Sidebar - PRIMARY_COLOR background, SIDEBAR_WIDTH wide
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(UIConstants.PRIMARY_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 720));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));

        // Call implementation-specific sidebar builder
        buildSidebar();

        // 3. Center Workspace - contentPanel white with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // 4. Bottom Status Bar - Light gray background
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(235, 235, 240));
        statusBar.setPreferredSize(new Dimension(1200, 25));
        statusBar.setBorder(new MatteBorder(1, 0, 0, 0, new Color(210, 210, 215)));

        statusLabel = new JLabel("  Ready");
        statusLabel.setFont(UIConstants.FONT_SMALL);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Add core components to BorderLayout root
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Initialize modules in subclasses
        initModules();
    }

    // Abstract methods to be overridden by roles
    protected abstract void buildSidebar();
    protected abstract void initModules();

    // Module switching API
    protected void showModule(String moduleName) {
        cardLayout.show(contentPanel, moduleName);
        updatePageTitle(moduleName);
    }

    // Update dynamic page title in header
    protected void updatePageTitle(String title) {
        lblPageTitle.setText(title);
    }

    // Sidebar button construction helper
    protected JButton createSidebarButton(String label) {
        JButton button = new JButton(label);
        button.setFont(UIConstants.FONT_BODY);
        button.setForeground(Color.WHITE);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add padding & layout constraints
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setPreferredSize(new Dimension(UIConstants.SIDEBAR_WIDTH, 45));

        // Slightly lighter blue hover effect
        Color hoverColor = new Color(0, 80, 160);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });

        return button;
    }
}
