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

package org.ajax4jsf.component;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.ajax4jsf.Messages;
import org.ajax4jsf.renderkit.AjaxRendererUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Component for append ajax functions to any control component. Append action
 * functionality to non-action control, setup javascript events for parent
 * component,
 * 
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.3 $ $Date: 2007/02/12 17:46:52 $
 * 
 */
public abstract class UIAjaxSupport extends AjaxActionComponent implements
		AjaxSupport {

	// ~ Static fields/initializers
	// ---------------------------------------------

	public static final String COMPONENT_TYPE = "org.ajax4jsf.Support";
	public static final String COMPONENT_FAMILY = "org.ajax4jsf.AjaxSupport";
	public static final String DEFAULT_RENDERER_TYPE = "org.ajax4jsf.components.AjaxSupportRenderer";
	public static final String AJAX_SUPPORT_SET = "com.exadel.components.ajax.support.";
	private static final Log log = LogFactory.getLog(UIAjaxSupport.class);

	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		try {
		    super.setValueExpression(name, binding);
		} catch (Exception e) {
		    log.error("Unable to set ValueExpression for name:"+name
		            +" with EL:\""+binding.getExpressionString()+"\""
		            +" which is "+(binding.isLiteralText()?"":"not ")+"literal,"
                    +" expected type:"+binding.getExpectedType()
		            +" acceptable type:"+binding.getType(getFacesContext().getELContext())
		            +" for instance of:"+this.getClass().getName()
		            +" Swallowed "+e.getClass().getSimpleName()
		            +" with message:"+e.getMessage());
		}
	}

	/**
	 * Create Special <code>ValueBinding</code> for build JavaScrept event
	 * code in parent component from this.
	 * 
	 * @return <code>EventValueBinding</code> based on properties of current
	 *         component
	 */
	private ValueExpression getEventValueBinding() {
		if (log.isDebugEnabled()) {
			log.debug(Messages.getMessage(Messages.CREATE_JAVASCRIPT_EVENT,
					getId()));
		}
		return new EventValueExpression(this);
	}

	/**
	 * @return JavaScript eventString. Rebuild on every call, since can be in
	 *         loop ( as in dataTable ) with different parameters.
	 */
	public String getEventString() {
		StringBuffer buildOnEvent = new StringBuffer();
		String onsubmit = getOnsubmit();
		// Insert script to call before submit ajax request.
		if (null != onsubmit) {
			buildOnEvent.append(onsubmit).append(";");
		}
		buildOnEvent.append(AjaxRendererUtils.buildOnEvent(this,
				getFacesContext(), getEvent(), true));
		String script = buildOnEvent.toString();
		return script;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#decode(javax.faces.context.FacesContext)
	 */
	public void decode(FacesContext context) {
		// Due to JSF RI 1.1 bug, clear cached clientId
		setId(getId());
		super.decode(context);
	}

	/**
	 * After nornal setting <code>parent</code> property in case of created
	 * component set Ajax properties for parent.
	 * 
	 * @see javax.faces.component.UIComponentBase#setParent(javax.faces.component.UIComponent)
	 */
	public void setParent(UIComponent parent) {
		super.setParent(parent);
		if (null != parent && parent.getFamily() != null) {
			if (log.isDebugEnabled()) {
				log.debug(Messages.getMessage(Messages.CALLED_SET_PARENT,
						parent.getClass().getName()));
			}

			setParentProperties(parent);
		}
	}

	public void setParentProperties(UIComponent parent) {
		ValueExpression valueBinding;
        if (null != getEvent()) {
            if (log.isDebugEnabled()) {
                log.debug(Messages.getMessage(
                        Messages.SET_VALUE_BINDING_FOR_EVENT, getEvent()));
            }
            // for non action/data components, or for non-default events - build
            // listener for this instance.
            valueBinding = getEventValueBinding();
            parent.setValueExpression(getEvent(), valueBinding);

        } else {
            valueBinding = this.getValueExpression("event");
        	    Object event;
        	    if (null != valueBinding)
                event = valueBinding.getValue(getFacesContext().getELContext());
            else
                event = getAttributes().get(this.getClass().getName()+".event");
            if (null != event) {
                StringBuffer eventCmd = AjaxRendererUtils.buildOnEvent(this,
                        getFacesContext(), event.toString(), true);
                eventCmd.append("; return false;");
                parent.getAttributes().put(""+event, eventCmd.toString());
            }
        }
    	}

	protected UIComponent getSingleComponent() {
		return getParent();
	}

}
