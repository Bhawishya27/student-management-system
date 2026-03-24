package SMS;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    public LoginScreen() {
        setTitle("Login - Student Management System");
        setSize(380, 230);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        panel.setBackground(new Color(245, 247, 252));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Student Management Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(18);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(18);
        JButton loginBtn = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(userLabel, gbc);
        gbc.gridx = 1;
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(passLabel, gbc);
        gbc.gridx = 1;
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> attemptLogin(userField, passField));
        passField.addActionListener(e -> attemptLogin(userField, passField));

        setContentPane(panel);
        setVisible(true);
    }

    private void attemptLogin(JTextField userField, JPasswordField passField) {
        String enteredUser = userField.getText().trim();
        String enteredPass = new String(passField.getPassword());

        if (USERNAME.equals(enteredUser) && PASSWORD.equals(enteredPass)) {
            dispose();
            new MainGUI();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Invalid username or password.",
                "Login Failed",
                JOptionPane.ERROR_MESSAGE
            );
            passField.setText("");
            passField.requestFocusInWindow();
        }
    }
}
