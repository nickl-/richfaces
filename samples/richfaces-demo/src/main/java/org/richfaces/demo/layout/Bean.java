/**
 * 
 */
package org.richfaces.demo.layout;

import java.io.Serializable;

/**
 * @author asmirnov
 *
 */
public class Bean implements Serializable {
    private static final long serialVersionUID = 1L;
	private String position="left";
	
	private String theme = "simple";
	
	private int width=1024;
	
	private int sidebarWidth=200;
	
	/**
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the sidebarWidth
	 */
	public int getSidebarWidth() {
		return sidebarWidth;
	}

	/**
	 * @param sidebarWidth the sidebarWidth to set
	 */
	public void setSidebarWidth(int sidebarWidth) {
		this.sidebarWidth = sidebarWidth;
	}

	/**
	 * @return the theme
	 */
	public String getTheme() {
		return theme;
	}

	/**
	 * @param theme the theme to set
	 */
	public void setTheme(String theme) {
		if("".equals(theme)){
			theme = null;
		}
		this.theme = theme;
	}
}
