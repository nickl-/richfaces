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
package org.richfaces.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

/**
 * Perform validation by Hibernate Validator annotations
 * 
 * @author asmirnov
 * 
 */
public class HibernateValidator extends ObjectValidator {

	static final String DEFAULT_VALIDATOR_MESSAGES = "org.hibernate.validator.resources.DefaultValidatorMessages";
	static final String VALIDATOR_MESSAGES = "ValidatorMessages";

	private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
	
	HibernateValidator() {
		super();
	}

	HibernateValidator(ObjectValidator parent){
		super(parent);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<String> validateGraph(FacesContext context, Object value,
			Set<String> profiles) {
		if (null == context) {
			throw new FacesException(INPUT_PARAMETERS_IS_NOT_CORRECT);
		}
		
		final Collection<String> validationMessages = new ArrayList<String>();
		
		if (null != value) {
		    Set<ConstraintViolation<Object>> errors = validator.validate(value);
		    errors.forEach(error -> validationMessages.add(error.getMessage()));
			if(null != parent){
				validationMessages.addAll(parent.validateGraph(context, value, profiles));
			}
		}
		
		return validationMessages;
	}

	/**
	 * Validate bean property in the base class aganist new value.
	 * @param beanClass
	 * @param property
	 * @param value
	 * 
	 * @return
	 */
	protected <T> Set<ConstraintViolation<T>> validateClass(FacesContext facesContext,
			Class<T> beanClass, String property, Object value) {
		
		return validator.validateValue(beanClass, property, value, Default.class);
	}

	/**
	 * Get ( or create ) {@link ClassValidator} for a given bean class.
	 * @param beanClass
	 * 
	 * @return
	 */
	protected Validator getValidator(
			FacesContext facesContext, Class<? extends Object> beanClass) {
		
		return this.validator;
	}

	/**
	 * @param facesContext
	 * @return
	 */
	protected ResourceBundle createHibernateMessages(FacesContext facesContext) {
		ResourceBundle bundle = getResourceBundle(facesContext, VALIDATOR_MESSAGES);
		ResourceBundle defaultMessagesBundle = getResourceBundle(facesContext, DEFAULT_VALIDATOR_MESSAGES);
		if(null != bundle && defaultMessagesBundle != null){
			bundle = new ResourceBundleChain(bundle, defaultMessagesBundle);
		} else if(null != defaultMessagesBundle){
			bundle = defaultMessagesBundle;
		}
		return bundle;
	}

	@Override
	protected Collection<String> validate(FacesContext facesContext, Object base, String property,
			Object value, Set<String> profiles) {
		final Collection<String> validationMessages = new ArrayList<String>();
		Set<ConstraintViolation<Object>> errors = validateBean(facesContext, base, property, value);
				if (errors.isEmpty()) {
					return null;
				} else {
					errors.forEach(error -> validationMessages.add(error.getMessage()));
					return validationMessages;
				}
			}

	/**
	 * Validate bean property of the base object aganist new value
	 * @param base
	 * @param property
	 * @param value
	 * 
	 * @return
	 */
	protected <T> Set<ConstraintViolation<T>> validateBean(FacesContext facesContext, T base, String property,
			Object value) {
		return validator.validateProperty(base, property, Default.class);
	}

	static class ResourceBundleChain extends ResourceBundle {
		
		private final ResourceBundle delegate;
		
		public ResourceBundleChain(ResourceBundle delegate, ResourceBundle parent) {
			this.delegate = delegate;
			setParent(parent);
		}

		@Override
		public Enumeration<String> getKeys() {
			return null!=delegate?delegate.getKeys():Collections.<String>enumeration(Collections.<String>emptyList());
		}

		@Override
		protected Object handleGetObject(String key) {
			try {
				return null != delegate ? delegate.getObject(key) : null;

			} catch (MissingResourceException e) {
				return null;
			}
		}

		@Override
		public Locale getLocale() {
			return null!=delegate?delegate.getLocale():null;
		}
	}

}
