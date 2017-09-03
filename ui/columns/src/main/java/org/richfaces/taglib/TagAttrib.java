package org.richfaces.taglib;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

import javax.faces.view.Location;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;


import javax.faces.view.facelets.TagAttributeException;

public class TagAttrib extends TagAttribute {
    private final boolean literal;

    private final String localName;

    private final Location location;

    private final String namespace;

    private final String qName;

    private final String value;

    private String string;

    public TagAttrib(Location location, String ns, String localName,
            String qName, String value) {
        this.location = location;
        this.namespace = ns;
        this.localName = localName;
        this.qName = qName;
        this.value = value;
        this.literal = value.contains("${") || value.contains("#{");
    }

    /**
     * If literal, return
     * {@link Boolean#getBoolean(java.lang.String) Boolean.getBoolean(java.lang.String)}
     * passing our value, otherwise call
     * {@link #getObject(FaceletContext, Class) getObject(FaceletContext, Class)}.
     * 
     * @see Boolean#getBoolean(java.lang.String)
     * @see #getObject(FaceletContext, Class)
     * @param ctx
     *            FaceletContext to use
     * @return boolean value
     */
    @Override
    public boolean getBoolean(FaceletContext ctx) {
        if (this.literal) {
            return Boolean.valueOf(this.value).booleanValue();
        } else {
            return ((Boolean) this.getObject(ctx, Boolean.class))
                    .booleanValue();
        }
    }

    /**
     * If literal, call
     * {@link Integer#parseInt(java.lang.String) Integer.parseInt(String)},
     * otherwise call
     * {@link #getObject(FaceletContext, Class) getObject(FaceletContext, Class)}.
     * 
     * @see Integer#parseInt(java.lang.String)
     * @see #getObject(FaceletContext, Class)
     * @param ctx
     *            FaceletContext to use
     * @return int value
     */
    @Override
    public int getInt(FaceletContext ctx) {
        if (this.literal) {
            return Integer.parseInt(this.value);
        } else {
            return ((Number) this.getObject(ctx, Integer.class)).intValue();
        }
    }

    /**
     * Local name of this attribute
     * 
     * @return local name of this attribute
     */
    @Override
    public String getLocalName() {
        return this.localName;
    }

    /**
     * The location of this attribute in the FaceletContext
     * 
     * @return the TagAttribute's location
     */
    @Override
    public Location getLocation() {
        return this.location;
    }

    /**
     * Create a MethodExpression, using this attribute's value as the expression
     * String.
     * 
     * @see ExpressionFactory#createMethodExpression(javax.el.ELContext,
     *      java.lang.String, java.lang.Class, java.lang.Class[])
     * @see MethodExpression
     * @param ctx
     *            FaceletContext to use
     * @param type
     *            expected return type
     * @param paramTypes
     *            parameter type
     * @return a MethodExpression instance
     */
    @Override
    public MethodExpression getMethodExpression(FaceletContext ctx, Class type, Class[] paramTypes) {
        try {
            ExpressionFactory f = ctx.getExpressionFactory();
            return f.createMethodExpression(ctx, this.value, type, paramTypes);
        } catch (Exception e) {
            throw new TagAttributeException(this, e);
        }
    }

    /**
     * The resolved Namespace for this attribute
     * 
     * @return resolved Namespace
     */
    @Override
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Delegates to getObject with Object.class as a param
     * 
     * @see #getObject(FaceletContext, Class)
     * @param ctx
     *            FaceletContext to use
     * @return Object representation of this attribute's value
     */
    @Override
    public Object getObject(FaceletContext ctx) {
        return this.getObject(ctx, Object.class);
    }

    /**
     * The qualified name for this attribute
     * 
     * @return the qualified name for this attribute
     */
    @Override
    public String getQName() {
        return this.qName;
    }

    /**
     * Return the literal value of this attribute
     * 
     * @return literal value
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * If literal, then return our value, otherwise delegate to getObject,
     * passing String.class.
     * 
     * @see #getObject(FaceletContext, Class)
     * @param ctx
     *            FaceletContext to use
     * @return String value of this attribute
     */
    @Override
    public String getValue(FaceletContext ctx) {
        if (this.literal) {
            return this.value;
        } else {
            return (String) this.getObject(ctx, String.class);
        }
    }

    /**
     * If literal, simply coerce our String literal value using an
     * ExpressionFactory, otherwise create a ValueExpression and evaluate it.
     * 
     * @see ExpressionFactory#coerceToType(java.lang.Object, java.lang.Class)
     * @see ExpressionFactory#createValueExpression(javax.el.ELContext,
     *      java.lang.String, java.lang.Class)
     * @see ValueExpression
     * @param ctx
     *            FaceletContext to use
     * @param type
     *            expected return type
     * @return Object value of this attribute
     */
    @Override
    public Object getObject(FaceletContext ctx, Class type) {
        if (this.literal) {
            if (String.class.equals(type)) {
                return this.value;
            } else {
                try {
                    return ctx.getExpressionFactory().coerceToType(this.value,
                            type);
                } catch (Exception e) {
                    throw new TagAttributeException(this, e);
                }
            }
        } else {
            ValueExpression ve = this.getValueExpression(ctx, type);
            try {
                return ve.getValue(ctx);
            } catch (Exception e) {
                throw new TagAttributeException(this, e);
            }
        }
    }

    /**
     * Create a ValueExpression, using this attribute's literal value and the
     * passed expected type.
     * 
     * @see ExpressionFactory#createValueExpression(javax.el.ELContext,
     *      java.lang.String, java.lang.Class)
     * @see ValueExpression
     * @param ctx
     *            FaceletContext to use
     * @param type
     *            expected return type
     * @return ValueExpression instance
     */
    @Override
    public ValueExpression getValueExpression(FaceletContext ctx, Class type) {
        try {
            ExpressionFactory f = ctx.getExpressionFactory();
            return f.createValueExpression(ctx, this.value, type);
        } catch (Exception e) {
            throw new TagAttributeException(this, e);
        }
    }

    /**
     * If this TagAttribute is literal (not #{..} or ${..})
     * 
     * @return true if this attribute is literal
     */
    @Override
    public boolean isLiteral() {
        return this.literal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (this.string == null) {
            this.string = this.location + " " + this.qName + "=\"" + this.value
                    + "\"";
        }
        return this.string;
    }

}
