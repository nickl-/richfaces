package org.richfaces.demo.toolBar;

import java.io.Serializable;

public class TbBean implements Serializable {
    private static final long serialVersionUID = 1L;
	private String groupSeparator;
	private String groupItemSeparator;
	public String getGroupItemSeparator() {
		return groupItemSeparator;
	}
	public void setGroupItemSeparator(String groupItemSeparator) {
		this.groupItemSeparator = groupItemSeparator;
	}
	public String getGroupSeparator() {
		return groupSeparator;
	}
	public void setGroupSeparator(String groupSeparator) {
		this.groupSeparator = groupSeparator;
	}
	

}
