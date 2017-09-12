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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:21 $
 *
 */
public class AjaxActionsRule extends MetaRule {

    private static final Log log = LogFactory.getLog(AjaxActionsRule.class);

    public final static Class<?>[] ACTION_SIG = new Class[0];

    public final static Class<?>[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };

    public final static AjaxActionsRule instance = new AjaxActionsRule();

    public AjaxActionsRule() {
        super();
    }

    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        String type = meta.getPropertyType(name).getSimpleName();
        if (!"MethodBinding".equals(type) && !"MethodExpression".equals(type))
            return null;
        return new Metadata() { 
            public void applyMetadata(FaceletContext context, Object instance) {
                try {
                    if ("action".equals(name))
                        ((ActionSource2) instance).setActionExpression(
                            attribute.getMethodExpression(context, String.class,
                                    AjaxActionsRule.ACTION_SIG));
                    else
                        ((ActionSource2) instance)
                        .addActionListener(new MethodExpressionActionListener(
                                attribute.getMethodExpression(context, Object.class,
                                    AjaxActionsRule.ACTION_LISTENER_SIG)));
                } catch (Exception e) {
                    log.error("Unable to get MethodExpression for name:"+name
                            +" with EL:\""+attribute.getValue()+"\""
                            +" which is "+(attribute.isLiteral()?"":"not ")+"literal,"
                            +" property type"+meta.getPropertyType(name)
                            +" acceptable type:"+attribute.getObject(context).getClass().getName()
                            +" for instance of:"+instance.getClass().getName()
                            +" Swallowed "+e.getClass().getSimpleName()
                            +" with message:"+e.getMessage());
                }
            }
        };
    }

}
