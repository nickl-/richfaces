/**
 * License Agreement.
 *
 * Rich Faces - Natural Ajax for Java Server Faces (JSF)
 *
 * Copyright (C) 2007 Exadel, Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

package org.ajax4jsf.templatecompiler.elements.html;

import java.util.Formatter;

import org.ajax4jsf.templatecompiler.builder.CompilationContext;
import org.ajax4jsf.templatecompiler.el.ELParser;
import org.ajax4jsf.templatecompiler.elements.TemplateElementBase;
import org.w3c.dom.Node;

/**
 * @author shura
 * 
 */
public class CommentElement extends TemplateElementBase {

	private String htmlText;

	public CommentElement(Node element, CompilationContext componentBean) {
		super(element, componentBean);
		this.htmlText = element.getNodeValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ajax4jsf.templatecompiler.elements.TemplateElement#getBeginElement()
	 */
	public String getBeginElement() {
		String retValue = null;
		if ((null != this.htmlText) && (this.htmlText.length() > 0)) {
			Object[] params = { ELParser.compileEL(this.htmlText, this.getComponentBean()) };
			retValue = new Formatter().format(
					"writer.writeComment(convertToString(%s));\n", params)
					.toString();
		}
		return retValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ajax4jsf.templatecompiler.elements.TemplateElement#getEndElement()
	 */
	public String getEndElement() {
		// Do nothitg - text not have childs
		return null;
	}

}
