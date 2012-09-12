package org.eclipse.gef4.graphics;

/**
 * @author mwienand
 * 
 */
public abstract class AbstractGraphicsProperties implements IGraphicsProperties {

	/**
	 * <p>
	 * Flag, that indicates if this {@link AbstractGraphicsProperties} is
	 * currently active on an {@link IGraphics}.
	 * </p>
	 * 
	 * <p>
	 * Note that an {@link AbstractGraphicsProperties} is active on creation.
	 * </p>
	 * 
	 * @see IGraphicsProperties#activate()
	 * @see IGraphicsProperties#deactivate()
	 * @see IGraphicsProperties#isActive()
	 * @see IGraphics#pushState()
	 * @see IGraphics#popState()
	 */
	protected boolean active = true;

	public void activate() {
		if (active) {
			throw new IllegalStateException(
					"Cannot activate already active IGraphicsProperties.");
		}
		active = true;
	}

	public void deactivate() {
		if (!active) {
			throw new IllegalStateException(
					"Cannot deactivate already inactive IGraphicsProperties.");
		}
		active = false;
	}

	public boolean isActive() {
		return active;
	}

}