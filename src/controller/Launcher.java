package controller;

import java.util.Date;

import controller.LoginController.LogLevel;
import model.UserModel;
import view.LoginView;

public class Launcher {

	public static void main(String[] args) {
		UserModel testUser = new UserModel("Admin", "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8", 
				new Date(2026, 11, 12, 10, 54, 15), new Date(2026, 11, 12, 10, 55, 55), true);
		
//		System.out.println(testUser.getUserName());
//		System.out.println(testUser.getPwHash());
//		testUser.setUserName("Hacker");
//		testUser.setPwHash("7f6c6ca14fb74cd9e78d20b91359ff6be2f8a61edbdb5501cd584bee41365cde");
//		System.out.println(testUser.getUserName());
//		System.out.println(testUser.getPwHash());
		
		LoginController loginController = new LoginController();
		loginController.writeActivityToLogFile(LogLevel.INFO, "Anwendung gestartet");
		new LoginView();
		
	}

}
