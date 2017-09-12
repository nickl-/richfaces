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

package org.ajax4jsf.taglib.html.facelets;

import java.io.IOException;
import java.util.Arrays;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource2;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIGraphic;
import javax.faces.component.UIParameter;
import javax.faces.component.UISelectBoolean;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagHandler;

import org.ajax4jsf.renderkit.RendererUtils;
import org.ajax4jsf.taglib.html.jsp.AjaxSupportTag;
import org.ajax4jsf.webapp.taglib.AjaxComponentHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * "proxy" class for creating UIAjaxSupport component as facet for it's parent.
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:22 $
 *
 */
public class AjaxSupportHandler extends TagHandler {

    private static final Log log = LogFactory.getLog(AjaxSupportHandler.class);

    /**
     * A UIComponent for capturing a child UIComponent, representative of the
     * desired Facet
     * 
     * @author Jacob Hookom
     * 
     */
    private final static class UIFacet extends UIComponentBase {
        public String getFamily() {
            return null;
        }
    }

	/**
	 * Real tag handler for create component.
	 */
	private TagHandler _ajaxSupportHandler;
	
	private TagAttribute _event;

// Somewhere between AjaxSupport and AjaxSupportTag is the proper set properties solution
//	class AjaxSupport extends AjaxSupportTag {
//	    protected void setProperties(UIComponent component) {
//	        super.setProperties(component);
//	    }
//
//	}
//	private AjaxSupport _props = new AjaxSupport();
	/**
	 * @param config
	 */
	public AjaxSupportHandler(ComponentConfig config) {
		super(config);
		_event = getRequiredAttribute("event");
		_ajaxSupportHandler = new AjaxComponentHandler(config);

//        try {
//                BeanUtils.setProperty(_props, t.getLocalName(), t.getValue());
//                } catch(Exception e) {
//            }
//        }
	}

	private FaceletContext getFaceletCantext() {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    if (null != facesContext)
	        return (FaceletContext) facesContext.getAttributes().get("javax.faces.FACELET_CONTEXT");
	    return null;
	}

	/*
	 * Duplicated from javax.faces.webapp.UIComponentTagBase; needs refactoring
	 */
	protected void setValueProperty(UIComponent component, ValueExpression expression) {
        if (expression != null) {
            String value = expression.getExpressionString();
            if (!expression.isLiteralText()) {
                    component.setValueExpression("value", expression);
            } else if (component instanceof UICommand) {
                    ((UICommand) component).setValue(value);
            } else if (component instanceof UIParameter) {
                    ((UIParameter) component).setValue(value);
            } else if (component instanceof UISelectBoolean) {
                    ((UISelectBoolean) component).setValue(Boolean.valueOf(value));
            } else if (component instanceof UIGraphic) {
                    ((UIGraphic) component).setValue(value);
            }
            // Since many input components are ValueHolders the special
            // components
            // must come first, ValueHolder is the last resort.
            else if (component instanceof ValueHolder) {
                    ((ValueHolder) component).setValue(value);
            } else {
                    component.getAttributes().put("value", value);
            }
        }
	}

	public void applyProperties(UIComponent component) {
        forloop:
        for (TagAttribute t : tag.getAttributes().getAll()) {
            try {
                Class<?> type = String.class;

                switch (t.getLocalName()) {
                    case "action":
                        if (component instanceof ActionSource2)
                        ((ActionSource2) component).setActionExpression(t.getMethodExpression(
                                      getFaceletCantext(), null, new Class[0]));
                        continue forloop;
                    case "actionListener":
                        if (component instanceof ActionSource2)
                        ((ActionSource2) component).addActionListener(
                              new MethodExpressionActionListener(
                                      t.getMethodExpression(getFaceletCantext(), null,
                                              new Class[] { ActionListener.class })));
                        continue forloop;
                    case "value":
                        setValueProperty(component, 
                                t.getValueExpression(getFaceletCantext(), String.class));
                        continue forloop;
                    case "ajaxSingle": case "disableDefault": case "bypassUpdates": case "inView":
                    case "immediate": case "limitToList": case "disabled": case "ignoreDupResponses": 
                    case "rendered":                        
                        type = Boolean.class;
                        break;
                    case "requestDelay": case "timeout":
                        type = Integer.class;
                        break;
                }

                if (null != getFaceletCantext())
                    component.setValueExpression(t.getLocalName(),
                            t.getValueExpression(getFaceletCantext(), type));
                else
                    component.getAttributes().put(component.getClass().getName()+"."
                            + t.getLocalName(), t.getValue());
            } catch (Exception e) {
                log.error("Unable to apply TagAttribute for name:"+t.getLocalName()
                        +" with EL:\""+t.getValue()+"\""
                        +" which is "+(t.isLiteral()?"":"not ")+"literal,"
                        +" acceptable type:"+t.getObject(getFaceletCantext()).getClass().getName()
                        +" for instance of:"+component.getClass().getName()
                        +" Swallowed "+e.getClass().getSimpleName()
                        +" with message:"+e.getMessage());
            }
        }
	}

	/* (non-Javadoc)
	 * @see javax.faces.view.facelets.FaceletHandler#apply(javax.faces.view.facelets.FaceletContext, javax.faces.component.UIComponent)
	 */
	public void apply(FaceletContext ctx, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {

        UIFacet facet = new UIFacet();
        // Find facet for support component
        String facetName = AjaxSupportTag.AJAX_SUPPORT_FACET+_event.getValue();
        UIComponent component = parent.getFacet(facetName);

        if (null != component) {
            parent.getFacets().remove(facetName);
            facet.getChildren().add(component);
        }

        this._ajaxSupportHandler.apply(ctx, facet);
        component = (UIComponent) facet.getChildren().get(0);
        this.applyProperties(component);
        parent.getFacets().put(facetName, component);

	}

}
