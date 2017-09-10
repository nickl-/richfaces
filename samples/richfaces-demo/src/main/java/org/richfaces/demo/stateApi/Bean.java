package org.richfaces.demo.stateApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bean implements Serializable {
    private static final long serialVersionUID = 1L;

	private String name;
	
	private String password;
	
	private String confirmPassword;
	
	private List<User> users = new ArrayList<User>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
