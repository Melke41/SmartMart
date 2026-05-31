package smartmart.ui;

import smartmart.exception.SmartMartException;
import smartmart.model.Admin;
import smartmart.model.Cashier;
import smartmart.model.Manager;
import smartmart.model.User;
import smartmart.service.AuthService;
import smartmart.util.UIConstants;
import smartmart.util.UIHelper;
import smartmart.ui.admin.AdminDashboardFrame;
import smartmart.ui.manager.ManagerDashboardFrame;
import smartmart.ui.cashier.CashierDashboardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {
    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JLabel lblError;
    private final JLabel lblStatus;
    private final AuthService authService;

    public LoginFrame() {
        super("SmartMart — Login");
        this.authService = new AuthService();

        // Frame settings
        setSize(1000, 620);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(UIHelper.createAppIcon().getImage());

        // Left Panel - solid primary color background
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(UIConstants.PRIMARY_COLOR);
        leftPanel.setMinimumSize(new Dimension(300, 400));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JLabel lblAppName = new JLabel("SmartMart");
        lblAppName.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblAppName.setForeground(Color.WHITE);
        lblAppName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblAppSubtitle = new JLabel("Retail Store Management System");
        lblAppSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblAppSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblAppSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTagline = new JLabel("Manage Smarter. Sell Faster.");
        lblTagline.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblTagline.setForeground(Color.WHITE);
        lblTagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel featuresPanel = new JPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));
        featuresPanel.setBackground(UIConstants.PRIMARY_COLOR);
        featuresPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblFeat1 = new JLabel("\u2713  Role-based access control");
        lblFeat1.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFeat1.setForeground(Color.WHITE);
        lblFeat1.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        
        JLabel lblFeat2 = new JLabel("\u2713  Real-time inventory tracking");
        lblFeat2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFeat2.setForeground(Color.WHITE);
        lblFeat2.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        
        JLabel lblFeat3 = new JLabel("\u2713  Automated sales reporting");
        lblFeat3.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblFeat3.setForeground(Color.WHITE);
        lblFeat3.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));

        featuresPanel.add(lblFeat1);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        featuresPanel.add(lblFeat2);
        featuresPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        featuresPanel.add(lblFeat3);

        JLabel lblVersion = new JLabel("v1.0  |  AAU CoSc2051");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(255, 255, 255, 160));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblAppName);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(lblAppSubtitle);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(lblTagline);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        leftPanel.add(featuresPanel);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblVersion);

        // Right Panel - light grey background with card
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        rightPanel.setMinimumSize(new Dimension(400, 400));

        // Styled Card
        JPanel cardPanel = UIHelper.createCard();
        cardPanel.setPreferredSize(new Dimension(420, 430));
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                cardPanel.getBorder(),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JLabel lblWelcome = UIHelper.createTitleLabel("Welcome Back");
        lblWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSignIn = UIHelper.createSubtitleLabel("Sign in to your account");
        lblSignIn.setFont(UIConstants.FONT_BODY);
        lblSignIn.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUserTag = new JLabel("Username");
        lblUserTag.setFont(UIConstants.FONT_BUTTON);
        lblUserTag.setForeground(UIConstants.TEXT_PRIMARY);
        lblUserTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = UIHelper.createStyledTextField();
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FORM_FIELD_HEIGHT));

        JLabel lblPassTag = new JLabel("Password");
        lblPassTag.setFont(UIConstants.FONT_BUTTON);
        lblPassTag.setForeground(UIConstants.TEXT_PRIMARY);
        lblPassTag.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = UIHelper.createStyledPasswordField();
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.FORM_FIELD_HEIGHT));

        btnLogin = UIHelper.createPrimaryButton("Login");
        btnLogin.setBackground(UIConstants.PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setContentAreaFilled(true);
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(UIConstants.FONT_BUTTON);
        btnLogin.setPreferredSize(new Dimension(300, 42));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, UIConstants.BUTTON_HEIGHT));

        lblError = new JLabel(" ");
        lblError.setFont(UIConstants.FONT_SMALL);
        lblError.setForeground(UIConstants.DANGER_COLOR);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblError.setVisible(false);

        lblStatus = new JLabel(" ");
        lblStatus.setFont(UIConstants.FONT_SMALL);
        lblStatus.setForeground(UIConstants.PRIMARY_COLOR);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblStatus.setVisible(false);

        // Add components to card
        cardPanel.add(lblWelcome);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(lblSignIn);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(lblUserTag);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(txtUsername);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        cardPanel.add(lblPassTag);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        cardPanel.add(txtPassword);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(lblStatus);
        cardPanel.add(lblError);

        rightPanel.add(cardPanel);

        // Combine left and right panels using JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.38); // divider at 38% from left
        splitPane.setEnabled(false); // non-movable
        splitPane.setDividerSize(0); // hide handle
        splitPane.setBorder(null); // remove default split pane border

        add(splitPane, BorderLayout.CENTER);

        // Event Handling
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // KeyListener on password field for Enter key
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showErrorMessage("Please enter both username and password.");
            return;
        }

        // Disable input during processing
        setInputsEnabled(false);
        showStatusMessage("Logging in...");
        hideErrorMessage();

        // Perform authentication on SwingWorker (off EDT)
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                // Call authentication service
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    hideStatusMessage();
                    setInputsEnabled(true);

                    if (user != null) {
                        routeUserDashboard(user);
                    } else {
                        showErrorMessage("Invalid username or password.");
                    }
                } catch (Exception ex) {
                    hideStatusMessage();
                    setInputsEnabled(true);
                    Throwable cause = ex.getCause();
                    if (cause instanceof SmartMartException) {
                        showErrorMessage(cause.getMessage());
                    } else {
                        showErrorMessage("Authentication error: " + ex.getMessage());
                    }
                }
            }
        };

        worker.execute();
    }

    private void routeUserDashboard(User user) {
        // Based on user role, initialize the corresponding dashboard
        switch (user.getRole()) {
            case ADMIN:
                if (user instanceof Admin) {
                    new AdminDashboardFrame((Admin) user).setVisible(true);
                } else {
                    new AdminDashboardFrame(new Admin(user.getUserId(), user.getUsername(), user.getPassword(), user.getRole(), user.getFullName(), user.isActive())).setVisible(true);
                }
                break;
            case MANAGER:
                if (user instanceof Manager) {
                    new ManagerDashboardFrame((Manager) user).setVisible(true);
                } else {
                    new ManagerDashboardFrame(new Manager(user.getUserId(), user.getUsername(), user.getPassword(), user.getRole(), user.getFullName(), user.isActive())).setVisible(true);
                }
                break;
            case CASHIER:
                if (user instanceof Cashier) {
                    new CashierDashboardFrame((Cashier) user).setVisible(true);
                } else {
                    new CashierDashboardFrame(new Cashier(user.getUserId(), user.getUsername(), user.getPassword(), user.getRole(), user.getFullName(), user.isActive())).setVisible(true);
                }
                break;
            default:
                showErrorMessage("Unknown user role: " + user.getRole());
                return;
        }
        // Dispose login frame
        dispose();
    }

    private void setInputsEnabled(boolean enabled) {
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
    }

    private void showErrorMessage(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
        revalidate();
        repaint();
    }

    private void hideErrorMessage() {
        lblError.setVisible(false);
        revalidate();
        repaint();
    }

    private void showStatusMessage(String msg) {
        lblStatus.setText(msg);
        lblStatus.setVisible(true);
        revalidate();
        repaint();
    }

    private void hideStatusMessage() {
        lblStatus.setVisible(false);
        revalidate();
        repaint();
    }
}
