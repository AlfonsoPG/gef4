/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 * 
 *******************************************************************************/
package org.eclipse.gef4.swtfx.event;

import org.eclipse.gef4.swtfx.IFigure;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.swt.SWT;

public class FocusTraversalDispatcher extends AbstractEventDispatcher {

	public FocusTraversalDispatcher(INode target) {
		// TODO: Do we need to know the "target"?
	}

	@Override
	public Event dispatchBubblingEvent(Event event) {
		return event;
	}

	@Override
	public Event dispatchCapturingEvent(Event event) {
		if (event.getEventType() == TraverseEvent.ANY) {
			TraverseEvent traverseEvent = (TraverseEvent) event;

			IEventTarget target = traverseEvent.getTarget();

			if (target instanceof IFigure) {
				if (!((IFigure) target).isFocusTraversable()) {
					traverseEvent.consume();
					return traverseEvent;
				}
				target = ((IFigure) target).getParentNode();
			}

			if (target instanceof IParent) {
				IParent g = (IParent) target;
				if (g.isFocusTraversable()) {
					boolean directionNext = traverseEvent.getDetail() == SWT.TRAVERSE_ARROW_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_PAGE_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_TAB_NEXT
							|| traverseEvent.getDetail() == SWT.TRAVERSE_RETURN;

					IFigure nextFocusFigure = trav(directionNext, g);
					g.getScene().setFocusTarget(nextFocusFigure);

					// TODO g.requestRedraw();
					traverseEvent.consume();
				}
			} else {
				// event.consume();
				return event;
			}
		}

		return event;
	}

	private IFigure trav(boolean directionNext, IParent container) {
		return null;
	}

}
