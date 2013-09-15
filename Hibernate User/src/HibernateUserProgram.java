import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import model.Account;

import org.hibernate.Session;
import org.hibernate.Transaction;

import common.HibernateUtil;
import controller.AccountController;
import controller.MySystemControllerWrapper;


public class HibernateUserProgram {
	
	public static void main(String[] args) {
		doInitialization();
		
		doGetAccounts();
		doInsertAccount();
	}

	private static void doInsertAccount() {
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			
			AccountController controller = MySystemControllerWrapper.getDefaultAccountController(session);
			
			controller.insertAccount(new Account("Woody", "123456"));
			
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			if (tx != null)
				tx.rollback();
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static void doGetAccounts() {
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			
			AccountController controller = new AccountController(session);
			
			for (Account account : controller.getAccounts()) {
				System.out.println(String.format("Account\tUsername:%s\tPassword:%s", account.getUsername(), account.getPassword()));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}

	private static void doInitialization() {
		HibernateUtil.createSessionFactoryBuilder()
//			.setPropertyConfiguration("hibernate.cfg.xml")	// [Hibernate User]/hibernate.cfg.xml is used.
//			.setPropertyConfiguration(new File("P:\\hibernate.cfg.xml"))
			.setPropertyConfiguration(createURL())
			.addConfiguration("/model/mapping.xml")
//			.addConfiguration(new File("P:\\mapping.xml"))
			.build();
	}

	private static URL createURL() {
		try {
			return new URL("http://localhost/hibernate.cfg.xml");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
