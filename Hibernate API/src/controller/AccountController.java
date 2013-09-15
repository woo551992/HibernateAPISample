package controller;
import java.util.List;

import javax.management.relation.Role;

import model.Account;

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
}
