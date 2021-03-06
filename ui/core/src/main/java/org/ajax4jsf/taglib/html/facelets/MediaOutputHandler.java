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

import java.io.OutputStream;

import org.ajax4jsf.component.UIMediaOutput;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;

/**
 * @author shura (latest modification by $Author: alexsmirnov $)
 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:22 $
 *
 */
public class MediaOutputHandler extends ComponentHandler {

	private static final MetaRule mmediaMetaRule = new MMediaMetaRule();
	/**
	 * @param config
	 */
	public MediaOutputHandler(ComponentConfig config) {
		super(config);
		
	}

	/* (non-Javadoc)
	 * @see org.ajax4jsf.tag.AjaxComponentHandler#createMetaRuleset(java.lang.Class)
	 */
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset metaRules = super.createMetaRuleset(type);
		metaRules.addRule(mmediaMetaRule);
		return metaRules;
	}

	/**
	 * @author shura (latest modification by $Author: alexsmirnov $)
	 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:22 $
	 *
	 */
	static class MMediaMetaRule extends MetaRule{

		/**
		 * 
		 */
		public MMediaMetaRule() {
			super();
		}

		/* (non-Javadoc)
		 * @see javax.faces.view.facelets.MetaRule#applyRule(java.lang.String, javax.faces.view.facelets.TagAttribute, javax.faces.view.facelets.MetadataTarget)
		 */
		public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
	        if (meta.isTargetInstanceOf(UIMediaOutput.class)) {


	            if ("createContent".equals(name)) {
	                    return new MMediaActionMapper(attribute);
	            }
	        }
			return null;
		}

	}
	/**
	 * @author shura (latest modification by $Author: alexsmirnov $)
	 * @version $Revision: 1.1.2.1 $ $Date: 2007/02/01 15:31:22 $
	 *
	 */
	static class MMediaActionMapper extends Metadata {

		private static final Class<?>[] MMEDIA_ACTION_SIG = new Class[] {OutputStream.class,Object.class};

		private final TagAttribute _send;
		/**
		 * @param attribute
		 */
		public MMediaActionMapper(TagAttribute attribute) {
			_send = attribute;
		}

		/* (non-Javadoc)
		 * @see javax.faces.view.facelets.Metadata#applyMetadata(javax.faces.view.facelets.FaceletContext, java.lang.Object)
		 */
		public void applyMetadata(FaceletContext ctx, Object instance) {
		    ((UIMediaOutput) instance).setCreateContentExpression(this._send.getMethodExpression(ctx, null,
		    MMEDIA_ACTION_SIG));
		}

	}

}
