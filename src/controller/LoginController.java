package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import model.UserModel;
import view.LoginView;

public class LoginController {
	
	private UserModel userModel;
	private LoginView loginView;
	
	// constructor
	public LoginController(UserModel userModel, LoginView loginView) {
		super();
		this.userModel = userModel;
		this.loginView = loginView;
	}
	
	// standard constructor (overloading)
	public LoginController() {
		super();
	}
	
	// path for logging
	private static final Path LOG_FILE_PATH = Path.of("logs", "local_activity.log");
	
	// formatter for timestamps
	private static final DateTimeFormatter DATE_FORMAT =
			DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
	
	// syslog levels definition by severity
	public enum LogLevel {
		EMERGENCY(0),
		ALERT(1),
		CRITICAL(2),
		ERROR(3),
		WARNING(4),
		NOTICE(5),
		INFO(6),
		DEBUG(7);
		
		private final int severity;
		
		LogLevel(int severity) {
			this.severity = severity;
		}
		
		public int getSeverity() {
			return severity;
		}
	}
	
	// getter/setter for referring objects
	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public LoginView getLoginView() {
		return loginView;
	}

	public void setLoginView(LoginView loginView) {
		this.loginView = loginView;
	}
	
	// writes a new log entry
	public void writeActivityToLogFile(LogLevel level, String message) {		
		try {
			// create a directory if log not existing
			Files.createDirectories(LOG_FILE_PATH.getParent());
			
			// time stamp
			String timestamp = LocalDateTime.now().format(DATE_FORMAT);
			
			// create composite log entry
			String logEntry = String.format(
					// Timestamp %s, [severity level - Description], Message %s
					// 11.11.2026 12:05:05 [1 - ALERT] System breached!
					"%s [%d - %s] %s",
					timestamp,
					level.getSeverity(),
					level.name(),
					message
				);
			
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(
									LOG_FILE_PATH.toFile(), true),
							StandardCharsets.UTF_8))) {
				// writing log-entry
				writer.write(logEntry);
				// "pressing Enter" - new line
				writer.newLine();
			}
			
		} catch (IOException io) {
			System.err.println("Fehler beim Schreiben der Nachricht: " + io.getMessage());
		}
	}

	// read log data
	public void readActivityLogFile() {
		if (!Files.exists(LOG_FILE_PATH)) {
			System.out.println("Keine Log-Datei vorhanden!");
			return;
		}
		
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(LOG_FILE_PATH.toFile()),
						StandardCharsets.UTF_8))) {
			String line;
			
			// continue with reading if more lines exist
			while ((line = reader.readLine()) != null) {
				// TODO: write to AdminView?
				System.out.println(line);
			}
			
		} catch (IOException io) {
			System.err.println("Fehler beim Lesen der Nachricht: " + io.getMessage());
		}
	}
}
