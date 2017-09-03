package org.ajax4jsf.taglib.html.facelets;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for wrapping another VariableMapper with a new context,
 * represented by a {@link java.util.Map Map}. Modifications occur to the Map
 * instance, but resolve against the wrapped VariableMapper if the Map doesn't
 * contain the ValueExpression requested.
 *
 * @author Jacob Hookom
 * @version $Id$
 */
public class VariableMapperWrapper extends VariableMapper {

    private final VariableMapper target;

    private Map vars;

    /**
     *
     */
    public VariableMapperWrapper(VariableMapper orig) {
        super();
        this.target = orig;
    }

    /**
     * First tries to resolve agains the inner Map, then the wrapped
     * ValueExpression.
     *
     * @see javax.el.VariableMapper#resolveVariable(java.lang.String)
     */
    public ValueExpression resolveVariable(String variable) {
        ValueExpression ve = null;
        try {
            if (this.vars != null) {
                ve = (ValueExpression) this.vars.get(variable);
            }
            if (ve == null) {
                return this.target.resolveVariable(variable);
            }
            return ve;
        } catch (StackOverflowError e) {
            throw new ELException("Could not Resolve Variable [Overflow]: "
                                  + variable, e);
        }
    }

    /**
     * Set the ValueExpression on the inner Map instance.
     *
     * @see javax.el.VariableMapper#setVariable(java.lang.String,
     *      javax.el.ValueExpression)
     */
    public ValueExpression setVariable(String variable,
                                       ValueExpression expression) {
        if (this.vars == null) {
            this.vars = new HashMap();
        }
        return (ValueExpression) this.vars.put(variable, expression);
	}
}
