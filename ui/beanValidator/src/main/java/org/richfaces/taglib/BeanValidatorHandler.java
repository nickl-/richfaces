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

import java.util.Set;

import javax.faces.validator.Validator;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.ValidatorConfig;
import javax.faces.view.facelets.ValidatorHandler;

import org.ajax4jsf.renderkit.AjaxRendererUtils;
import org.richfaces.validator.FacesBeanValidator;

/**
 * @author asmirnov
 * 
 */
public class BeanValidatorHandler extends ValidatorHandler {


	private TagAttribute _profiles;

	/**
	 * @param config
	 */
	public BeanValidatorHandler(ValidatorConfig config) {
		super(config);
		_profiles = getAttribute("profiles");
	}

    /**
     * <p>Retrieve the id of the validator that is to be created and
     * added to the parent <code>EditableValueHolder</code>.  All
     * subclasses should override this method because it is important
     * for Facelets to have a unique way of identifying the validators
     * that are added to this <code>EditableValueHolder</code> and
     * allows exclusions to work properly. An exclusion is a validator
     * declaration that has the attribute "disabled" which resolves to
     * false, instructing Facelets not to register a default validator
     * with the same id.</p>
     */
    public String getValidatorId(FaceletContext ctx) {
        String validatorId = super.getValidatorId(ctx);
        if(null != _profiles){
	        FacesBeanValidator validator = (FacesBeanValidator) getAttribute(validatorId)
	        		.getValueExpression(ctx, FacesBeanValidator.class)
	        		.getValue(ctx.getFacesContext().getELContext());
			if(_profiles.isLiteral()){
				validator.setProfiles(AjaxRendererUtils.asSet(_profiles.getValue()));
			} else {
				validator.setProfiles(_profiles.getValueExpression(ctx, Set.class));
			}
			setAttributes(ctx, validator);
		}
        
        return validatorId;
    }


//	@Override
//	protected Validator createValidator(FaceletContext ctx) {
//		FacesBeanValidator validator = (FacesBeanValidator) ctx.getFacesContext()
//				.getApplication().createValidator(
//						FacesBeanValidator.BEAN_VALIDATOR_TYPE);
//		if(null != _profiles){
//			if(_profiles.isLiteral()){
//				validator.setProfiles(AjaxRendererUtils.asSet(_profiles.getValue()));
//			} else {
//				validator.setProfiles(_profiles.getValueExpression(ctx, Set.class));
//			}
//		}
//		return validator;
//	}

}
