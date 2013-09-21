import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Account;
import model.User;
import model.UserAge;
import model.UserId;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;

import common.HibernateUtil;
import controller.AccountController;
import controller.MySystemControllerWrapper;
import evo.util.Display;


public class HibernateUserProgram {
	static void print(Object object){System.out.println(object);}
	/** test only */
	static <K,V> Map<K, V> toMap(K[] keys, V[] values){		
		final Map<K, V> map = new HashMap<K, V>();
		for (int i = 0; i < keys.length; i++)
			map.put(keys[i], (i >= values.length ? null : values[i]));	// base on keys length, add null if index is exceed.
		
		return map;
	}
	
	public static void main(String[] args) {
		doInitialization();
		
		testForIntegerFlag();
//		doSearchAccounts();
//		doGetAccounts();
//		doInsertAccount();
	}

	static final int FLAG_A = 0x1 << 0;
	static final int FLAG_B = 0x1 << 1;
	static final int FLAG_C = 0x1 << 2;
	private static void testForIntegerFlag() {
		doForFlag(FLAG_A | FLAG_B);
	}
	private static void doForFlag(int flags){
		if ((flags & FLAG_A) == FLAG_A) {
			print("is FLAG_A");
		}
		if ((flags & FLAG_B) == FLAG_B) {
			print("is FLAG_B");
		}
		if ((flags & FLAG_C) == FLAG_C) {
			print("is FLAG_C");			
		}
	}
	
	/**
	 * 
	 * @codeSample
	 * <PRE>
	 * {@code
	 * static final int FLAG_A = 0x1;
	 * static final int FLAG_B = 0x1 << 1;
	 * static final int FLAG_C = 0x1 << 2;
	 * 
	 * void doSomethingForFlags(int flags){
	 * 	FlagHelper fl_helper = new FlagHelper(flags);
	 * 	if (fl_helper.contains(FLAG_A))
	 * 		doForA();
	 * 	if (fl_helper.contains(FLAG_B))
	 * 		doForB();
	 * 	if (fl_helper.contains(FLAG_C))
	 * 		doForC();
	 * }
	 * </PRE>
	 */
	static class FlagHelper{
		static int count = 0;
		public static int nextFlagConstant(){
			return 0x1 << count++;
		}
		private int flags;
//		public FlagHelper(){
//		}
		public FlagHelper(int flags){
			this.flags = flags;
		}

		public boolean contains(int flag){
			return (flags & flag) == flag;
		}
	}
	
	private static void doSearchAccounts() {		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		AccountController controller = new AccountController(session);
		
		Account criteria = createAccountCriteria();
		List<Account> accounts = controller.searchAccounts(criteria);
		
		Display.display(accounts);
	}
	
	

	private static Account createAccountCriteria() {
		Account account = new Account();
		account.setUsername("MyAccount");
		account.setPassword("MyPassword1");
		return account;
	}
//
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
