package model;

import java.util.Date;

public class UserModel {
	// attributes
	private String userName;
	private String pwHash;
	private Date registeredDate;
	private Date lastLoginDate;
	private boolean isAdmin;
	
	// constructor 
	public UserModel(String userName, String pwHash, Date registeredDate, Date lastLoginDate, boolean isAdmin) {
		super();
		this.userName = userName;
		this.pwHash = pwHash;
		this.registeredDate = registeredDate;
		this.lastLoginDate = lastLoginDate;
		this.isAdmin = isAdmin;
	}
	
	// access methods 
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwHash() {
		return pwHash;
	}

	public void setPwHash(String pwHash) {
		this.pwHash = pwHash;
	}

	public Date getRegisteredDate() {
		return registeredDate;
	}

	public void setRegisteredDate(Date registeredDate) {
		this.registeredDate = registeredDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	

}
