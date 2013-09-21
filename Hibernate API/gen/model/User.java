package model;

// Generated Sep 21, 2013 12:37:55 AM by Hibernate Tools 4.0.0

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User generated by hbm2java
 */
@Entity
@Table(name = "user", catalog = "database1")
public class User implements java.io.Serializable {

	private UserId id;
	private Long money;

	public User() {
	}

	public User(UserId id) {
		this.id = id;
	}

	public User(UserId id, Long money) {
		this.id = id;
		this.money = money;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "site", column = @Column(name = "site", nullable = false, length = 45)),
			@AttributeOverride(name = "location", column = @Column(name = "location", nullable = false, length = 45)),
			@AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, length = 45)) })
	public UserId getId() {
		return this.id;
	}

	public void setId(UserId id) {
		this.id = id;
	}

	@Column(name = "money", precision = 10, scale = 0)
	public Long getMoney() {
		return this.money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

}
