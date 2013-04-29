/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.richfaces.ui.ajax;

import java.util.LinkedList;
import java.util.Map;

import javax.faces.context.FacesContext;

/**
 * @author Nick Belaevski
 *
 */
final class AttachQueueStack {
    private static final String STACK_ATTRIBUTE_NAME = "org.richfaces.AttachQueueStack";
    private LinkedList<AttachQueueInfo> stack = null;

    public AttachQueueStack() {
        stack = new LinkedList<AttachQueueInfo>();
    }

    public static AttachQueueStack getStack(FacesContext context, boolean createIfNull) {
        Map<Object, Object> attributes = context.getAttributes();
        AttachQueueStack attachQueueStack = (AttachQueueStack) attributes.get(STACK_ATTRIBUTE_NAME);

        if (attachQueueStack == null && createIfNull) {
            attachQueueStack = new AttachQueueStack();
            attributes.put(STACK_ATTRIBUTE_NAME, attachQueueStack);
        }

        return attachQueueStack;
    }

    public void push(AttachQueueInfo info) {
        stack.addFirst(info);
    }

    public AttachQueueInfo pop() {
        return stack.removeFirst();
    }

    public AttachQueueInfo peek() {
        if (!stack.isEmpty()) {
            return stack.getFirst();
        } else {
            return null;
        }
    }
}