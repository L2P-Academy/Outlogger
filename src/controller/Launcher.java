package controller;

import java.util.Date;

import controller.LoginController.LogLevel;
import model.UserModel;
import view.LoginView;

public class Launcher {
	public static void main(String[] args) {
		UserModel testUser = new UserModel("", "", new Date(0), new Date(0), false);
		
		LoginController loginController = new LoginController(testUser, new LoginView());		
		loginController.writeActivityToLogFile(LogLevel.NOTICE, "Application started (LoginView)");
		
		// TODO: code for testing
//		System.out.println(testUser.getUserName());
//		System.out.println(testUser.getPwHash());
//		testUser.setUserName("Hacker");
//		testUser.setPwHash("7f6c6ca14fb74cd9e78d20b91359ff6be2f8a61edbdb5501cd584bee41365cde");
//		System.out.println(testUser.getUserName());
//		System.out.println(testUser.getPwHash());
		
	}

}