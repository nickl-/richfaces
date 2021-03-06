/**
 * 
 */
package org.richfaces.demo.validation;


import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author Ilya Shaikovsky
 *
 */
public class ValidationBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private String progressString="Fill the form please";
	
	@NotNull
	@Pattern(regexp=".*[^\\s].*", message="This string contain only spaces")
	@Size(min=3, max=12)
	private String name;
	@Pattern(regexp=".+@.+\\..+", message="Please provide a valid email address")
	@NotNull
	@Size(min=1)
	private String email;
	
	@NotNull
	@Min(18)
	@Max(100)
	private Integer age;
	
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

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
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
