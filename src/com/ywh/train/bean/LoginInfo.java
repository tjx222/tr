package com.ywh.train.bean;

import java.io.Serializable;

public class LoginInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -887230554445931968L;
	
	private String loginName;
	private String password;

	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
