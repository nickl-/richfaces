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

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.FaceletHandler;
import javax.faces.view.facelets.Tag;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributes;

import com.sun.faces.facelets.tag.TagAttributeImpl;
import com.sun.faces.facelets.tag.TagAttributesImpl;

/**
 * @author akolonitsky
 * @since Dec 20, 2009
 */
class ColumnHandlerComponentConfig implements ComponentConfig {

    private static final String ITERATION_INDEX_EXPRESSION = "#{" + ColumnsHandler.ITERATION_INDEX_VARIABLE + "}";

    private Tag tag;
    private final ComponentConfig config;

    ColumnHandlerComponentConfig(ComponentConfig config) {
        this.config = config;
        Tag initialTag = config.getTag();
        TagAttribute[] allInitialAttributes = initialTag.getAttributes().getAll();
        TagAttribute[] attributes = new TagAttributeImpl[allInitialAttributes.length];
        for (int i = 0; i < allInitialAttributes.length; i++) {
            TagAttribute initialAttribute = allInitialAttributes[i];
            String localName = initialAttribute.getLocalName();
            String attributeValue = initialAttribute.getValue();

            if ("id".equals(localName)) {
                attributeValue += ITERATION_INDEX_EXPRESSION;
            }

            attributes[i] = new TagAttributeImpl(initialAttribute.getLocation(), initialAttribute.getNamespace(),
                localName, initialAttribute.getQName(), attributeValue);
        }

        TagAttributes tagAttributes = new TagAttributesImpl(attributes);
        this.tag = new Tag(initialTag, tagAttributes);
    }

    public String getComponentType() {
        return config.getComponentType();
    }

    public String getRendererType() {
        return config.getRendererType();
    }

    public FaceletHandler getNextHandler() {
        return config.getNextHandler();
    }

    public Tag getTag() {
        return tag;
    }

    public String getTagId() {
        return config.getTagId();
    }

}
