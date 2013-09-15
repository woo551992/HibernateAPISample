package controller;

import org.hibernate.Session;

public class MySystemControllerWrapper {
	
	public static AccountController getDefaultAccountController(Session session){
		return new AccountController(session);
	}

}
