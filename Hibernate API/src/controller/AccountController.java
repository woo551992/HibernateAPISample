package controller;
import java.util.List;
import javax.management.relation.Role;

import model.Account;
import org.hibernate.Criteria;
import org.hibernate.Session;
import common.BaseController;

public class AccountController extends BaseController{
		
	public AccountController(Session session) {
		super(session);
	}

	public List<Account> getAccounts(){
		return list("from Account");	// BaseController.list, same as getSession().createQuery("from Account").list();
	}
	
	public List<Role> getRoles(){
		return list("from Role");
	}
	
	public Account getAccount(String username){
		return (Account) getSession().get(Account.class, username);
	}
	
	public void insertAccount(Account account){
		getSession().save(account);
	}
	
	public void saveAccount(Account account){
		getSession().update(account);
	}
	
	public List<Account> searchAccounts(Account accountCriteria){
		// old style, so fucking stupid if there are 100 fields
//		Map<String, Object> para = new HashMap<String, Object>();
//		String hql = 
//				"from Account" +
//				" where 1=1";
//		
//		if (criteria != null) {
//			if (criteria.getUsername() != null) {
//				hql += " and username=:username";
//				para.put("username", criteria.getUsername());
//			}
//			if (criteria.getPassword() != null) {
//				hql += " and password=:password";
//				para.put("password", criteria.getPassword());
//			}
//		}
//		
//		Query query = getSession().createQuery(hql);
//		
//		for (Entry<String, Object> entry : para.entrySet())
//			query.setParameter(entry.getKey(), entry.getValue());
//		
//		return list(query);
		
//		Criteria criteria = createSearchCriteria(SearchMode.BY_NON_NULL_FIELDS, Account.class, accountCriteria);
		Criteria criteria = createSearcher(SearchMode.BY_NON_NULL_FIELDS)
								.exclude("password")	// do not search for password property
								.createCriteria(Account.class, accountCriteria);
		// you can do additional filtering
		return list(criteria);
	}

	/** test only */
	public <T> List<T> basicSearch(Class<T> class1, T t) {
//		return list(createSearchCriteria(SearchMode.BY_NON_NULL_FIELDS, class1, t));
		return list(createSearcher(SearchMode.BY_NON_NULL_FIELDS).createCriteria(class1, t));
	}
}
