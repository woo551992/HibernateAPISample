package common;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @definition
 * Controller is the API which provide either internal or external database access. <br/>
 * You should implement controllers instead of writing code at Servlet. <br/>
 * It is recommended to extends this class for API implementation therefore we can add functions in this class for more convenient. <br/>
 * @codeSample
 * <PRE>
 * {@code 
 * public Account getAccount(String username){
 * 	return (Account) getSession().get(Account.class, username);
 * }
 * </PRE>
 */
public class BaseController {
	
	/**
	 * Every Controller need a Session to access database, 
	 * we don't {@link org.hibernate.SessionFactory #openSession()} every time in API methods
	 * so Session can be reused or transaction can be made by the user-class.
	 */
	private final Session session;

	public BaseController(Session session){
		this.session = session;
	}

	public Session getSession() {
		return session;
	}
	
	
	
	
//		Utilize		\\
	/**
	 * Same as {@link #list(Query)} with a basic HQL statement, the Session {@link #getSession()} is used.
	 * @codeSample
	 * List<Account> account = list("from Account");
	 */
	protected <T> List<T> list(String listQuery) {
		return list(getSession().createQuery(listQuery));
	}
	/**
	 * Call this method instead of {@link Query #list()} to ignore type safety warning
	 * @codeSample 
	 * List<Account> account = list(getSession().createQuery("from Account"));
	 */
	@SuppressWarnings("unchecked")
	protected static <T> List<T> list(Query listQuery){
		return listQuery.list();
	}

}
