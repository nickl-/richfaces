/**
 * 
 */
package org.richfaces.demo.comboBox;

import java.io.Serializable;

/**
 * @author Ilya Shaikovsky
 *
 */
public class ComboBoxBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private String value;
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ComboBoxBean() {
	}
}
