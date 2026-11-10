package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class LoginView extends JFrame{
	// attributes
	// containers
	private JPanel centerPanel, buttonPanel, loginPanel;
	
	// components
	private JLabel headerLabel, userNameLabel, passwordLabel;
	private JTextField userNameTextField;
	private JPasswordField passwordField;
	private JButton loginButton, registerButton, closeButton, showPwButton;
	
	public LoginView() {
		// initial window parameters
		setTitle("Outlogger - Security Simulator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setResizable(false);
		
		// background panel		
		centerPanel = new BackGroundPanel(new ImageIcon("res/cybersecurity_bg.png").getImage());

		headerLabel = new JLabel("OUTLOGGER", SwingConstants.CENTER);
		headerLabel.setForeground(new Color(10, 50, 100));
		headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
		
		// center panel
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.setBackground(new Color(47, 111, 222));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		// login panel
		loginPanel = new JPanel();
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
		loginPanel.setOpaque(false);
		
		userNameLabel = new JLabel("user name");
		userNameLabel.setForeground(Color.WHITE);
		userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		userNameTextField = new JTextField();
		userNameTextField.setPreferredSize(new Dimension(300, 30));
		userNameTextField.setMaximumSize(new Dimension(300, 30));
		userNameTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		// password
		passwordLabel = new JLabel("password");
		passwordLabel.setForeground(Color.WHITE);
		passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		passwordField = new JPasswordField();
		passwordField.setPreferredSize(new Dimension(300, 30));
		
		showPwButton = new JButton("<o>");
		
		// assemble login objects
		JPanel passwordRow = new JPanel(new BorderLayout(5, 0));
		passwordRow.setOpaque(false);
		passwordRow.setMaximumSize(new Dimension(300, 30));
		passwordRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		passwordRow.add(passwordField, BorderLayout.CENTER);
		passwordRow.add(showPwButton, BorderLayout.EAST);
		
		loginPanel.add(userNameTextField);
		loginPanel.add(userNameLabel);
		loginPanel.add(Box.createVerticalStrut(30));
		loginPanel.add(passwordRow);
		loginPanel.add(passwordLabel);
		
		centerPanel.add(loginPanel);
		
		// buttons
		buttonPanel = new JPanel(new FlowLayout());
		loginButton = new JButton("Login");
		beautifyButton(loginButton);
		registerButton = new JButton("Register");
		beautifyButton(registerButton);
		closeButton = new JButton("Close");
		beautifyButton(closeButton);
		
		buttonPanel.add(loginButton);
		buttonPanel.add(registerButton);
		buttonPanel.add(closeButton);
		buttonPanel.setOpaque(false);
		
		// final window parameters
		getContentPane().add(headerLabel, BorderLayout.NORTH);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void beautifyButton(JButton button) {
		button.setFocusPainted(false);
		button.setBackground(new Color(10, 50, 100));
		button.setForeground(Color.WHITE);
		button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));

		// Rounded Corners
		Border border = BorderFactory.createLineBorder(new Color(255, 255, 255), 2);
		Border roundedBorder = BorderFactory.createCompoundBorder(border,
				BorderFactory.createEmptyBorder(10, 20, 10, 20));
		button.setBorder(
				BorderFactory.createCompoundBorder(roundedBorder, BorderFactory.createEmptyBorder(5, 15, 5, 15)));

		// color change when MouseOver is happening
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(100, 149, 237));
			}

			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(new Color(10, 50, 100));
			}
		});
	}
}
