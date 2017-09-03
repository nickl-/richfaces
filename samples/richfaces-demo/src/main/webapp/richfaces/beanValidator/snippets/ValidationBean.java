/**
 * 
 */
package org.richfaces.demo.validation;

import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author Ilya Shaikovsky
 *
 */
public class ValidationBean {

	private String progressString="Fill the form please";
	
	@NotNull
	@Size(min=3, max=12)
	private String name;
	@Pattern(regexp=".+@.+\\..+", message="Please provide a valid email address")
	@NotNull
	@Size(min=1)
	private String email;
	@NotNull
	@Min(18)
	@Max(100)
	private int age;
	
	public ValidationBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
	public void success() {
		setProgressString(getProgressString() + "(Strored successfully)");
	}

	public String getProgressString() {
		return progressString;
	}

	public void setProgressString(String progressString) {
		this.progressString = progressString;
	}
}
