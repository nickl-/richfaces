/**
 * 
 */
package org.richfaces.taglib;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;

import com.sun.faces.util.Util;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
//import javax.faces.view.facelets.ComponentSupport;
//import javax.faces.view.facelets.ViewHandler;

/**
 * This handler does same work as Facelet's {@link ViewHandler} does to setup page encoding and content type.
 * 
 * @author asmirnov
 *
 */
public class PageTagHandler extends ComponentHandler {

	private TagAttribute locale;
	private TagAttribute contentType;
	private TagAttribute encoding;

	public PageTagHandler(ComponentConfig config) {
		super(config);
        this.locale = this.getAttribute("locale");
        this.contentType = this.getAttribute("contentType");
        this.encoding = this.getAttribute("encoding");
	}
	
	@Override
	public void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		super.onComponentPopulated(ctx, c, parent);
        UIViewRoot root = getViewRoot(ctx, parent);
        if (root != null) {
            if (this.locale != null) {
                root.setLocale(getLocale(ctx, this.locale));
            }
            if (this.contentType != null) {
                String v = this.contentType.getValue(ctx);
                ctx.getFacesContext().getExternalContext().getRequestMap().put("facelets.ContentType", v);
                root.getAttributes().put("contentType", v);
            }
            if (this.encoding != null) {
                String v = this.encoding.getValue(ctx);
                ctx.getFacesContext().getExternalContext().getRequestMap().put("facelets.Encoding", v);
                root.getAttributes().put("encoding", v);
            }
        }
	}
	
    /**
     * Tries to walk up the parent to find the UIViewRoot, if not found, then go
     * to FaceletContext's FacesContext for the view root.
     * 
     * @param ctx
     *            FaceletContext
     * @param parent
     *            UIComponent to search from
     * @return UIViewRoot instance for this evaluation
     */
    private UIViewRoot getViewRoot(FaceletContext ctx,
            UIComponent parent) {
        UIComponent c = parent;
        do {
            if (c instanceof UIViewRoot) {
                return (UIViewRoot) c;
            } else {
                c = c.getParent();
            }
        } while (c != null);
        return ctx.getFacesContext().getViewRoot();
    }
	
    /**
     * According to JSF 1.2 tag specs, this helper method will use the
     * TagAttribute passed in determining the Locale intended.
     * 
     * @param ctx
     *            FaceletContext to evaluate from
     * @param attr
     *            TagAttribute representing a Locale
     * @return Locale found
     * @throws TagAttributeException
     *             if the Locale cannot be determined
     */
    private Locale getLocale(FaceletContext ctx, TagAttribute attr)
            throws TagAttributeException {
        Object obj = attr.getObject(ctx);
        if (obj instanceof Locale) {
            return (Locale) obj;
        }
        if (obj instanceof String) {
            String s = (String) obj;
            try {
                return Util.getLocaleFromString(s);
            }
            catch(IllegalArgumentException iae) {
                throw new TagAttributeException(attr, "Invalid Locale Specified: " + s);
            }
        } else {
            throw new TagAttributeException(attr,
                    "Attribute did not evaluate to a String or Locale: " + obj);
        }
    }

}
