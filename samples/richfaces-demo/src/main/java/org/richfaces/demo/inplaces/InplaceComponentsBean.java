/**
 * 
 */
package org.richfaces.demo.inplaces;

import java.io.Serializable;

/**
 * @author Ilya Shaikovsky
 *
 */
public class InplaceComponentsBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private String inputValue;
	
	public String getInputValue() {
		return inputValue;
	}

	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	public InplaceComponentsBean() {
	}
}
