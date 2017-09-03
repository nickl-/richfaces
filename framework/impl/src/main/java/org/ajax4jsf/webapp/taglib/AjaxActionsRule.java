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

package org.ajax4jsf.webapp.taglib;

import javax.faces.component.ActionSource2;
import javax.faces.event.ActionEvent;
import javax.faces.event.MethodExpressionActionListener;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

/**
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:21 $
 *
 */
public class AjaxActionsRule extends MetaRule {

    public final static Class[] ACTION_SIG = new Class[0];

    public final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };

    public final static AjaxActionsRule instance = new AjaxActionsRule();

    public AjaxActionsRule() {
        super();
    }

    public Metadata applyRule(String name, TagAttribute attribute,
            MetadataTarget meta) {
    	
//		((ActionSource) instance).setActionListener(
//				new MethodBindingMethodExpressionAdaptor(
//            attribute.getMethodExpression(ctx, String.class,
//            		AjaxActionsRule.ACTION_SIG)));
    		return new Metadata() {
    			public void applyMetadata(FaceletContext ctx, Object instance) {
    				if ("action".equals(name)) 
    					((ActionSource2) instance).setActionExpression(
		                    attribute.getMethodExpression(ctx, String.class,
		                    		AjaxActionsRule.ACTION_SIG));
    				else
    					((ActionSource2) instance)
                        .addActionListener(new MethodExpressionActionListener(
                        		attribute.getMethodExpression(ctx, Object.class,
                            		AjaxActionsRule.ACTION_LISTENER_SIG)));
    			}
    		};
    }

}
