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

package org.richfaces.taglib;

import javax.faces.event.ActionEvent;

import org.ajax4jsf.webapp.taglib.AjaxComponentHandler;
import org.richfaces.component.UIDialogWindow;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.LegacyMethodBinding;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.ComponentConfig;

/**
 * @author Nick Belaevski - nbelaevski@exadel.com
 * created 08.12.2006
 * 
 */
public class DialogWindowComponentHandler extends AjaxComponentHandler {

	public DialogWindowComponentHandler(ComponentConfig componentConfig) {
		super(componentConfig);
	}

	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset metaRuleset = super.createMetaRuleset(type);
		metaRuleset.addRule(new MetaRule() {

			public Metadata applyRule(String name, final TagAttribute attribute, MetadataTarget meta) {
				if (meta.isTargetInstanceOf(UIDialogWindow.class)) {
					
					if ("closeWindowActionListener".equals(name)) {
						return new Metadata() {

							public void applyMetadata(FaceletContext context, Object instance) {
					            ((UIDialogWindow) instance).setCloseWindowActionListener(new LegacyMethodBinding(
					                    attribute.getMethodExpression(context, null,
					                            new Class[] { ActionEvent.class })));
							}
							
						};
					} else if ("closeWindowAction".equals(name)) {
							return new Metadata() {

								public void applyMetadata(FaceletContext context, Object instance) {
						            ((UIDialogWindow) instance).setCloseWindowAction(new LegacyMethodBinding(
						                    attribute.getMethodExpression(context, String.class,
						                            new Class[] { })));
								}
								
							};
						}
				}
				
				return null;
			}
			
		});
		
		return metaRuleset;
	}
}
