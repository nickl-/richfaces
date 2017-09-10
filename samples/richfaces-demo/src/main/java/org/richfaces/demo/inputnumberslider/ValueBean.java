package org.richfaces.demo.inputnumberslider;

import java.io.Serializable;

public class ValueBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private Integer value;

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
