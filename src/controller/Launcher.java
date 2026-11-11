package controller;

import controller.LoginController.LogLevel;
import view.LoginView;

public class Launcher {

	public static void main(String[] args) {
		LoginController loginController = new LoginController();
		loginController.writeActivityToLogFile(LogLevel.INFO, "Anwendung gestartet");
		new LoginView();
	}

}
