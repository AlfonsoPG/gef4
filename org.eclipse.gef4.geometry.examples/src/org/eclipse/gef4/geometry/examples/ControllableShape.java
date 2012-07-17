package org.eclipse.gef4.geometry.examples;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;

abstract public class ControllableShape {

	private boolean active;
	public int shapeColor;
	public int controlColor;
	public double controlRadius;
	public List<ControlPoint> controlPoints;

	public ControllableShape() {
		controlPoints = new ArrayList<ControlPoint>();
		shapeColor = SWT.COLOR_BLACK;
		controlColor = SWT.COLOR_BLUE;
		controlRadius = 5;
		active = true;
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		active = true;
	}

	public void deactivate() {
		active = false;
	}

	public void addControlPoints(Point... points) {
		for (Point p : points) {
			controlPoints.add(new ControlPoint(p));
		}
	}

	public void onResize(Rectangle bounds) {
		for (ControlPoint cp : controlPoints)
			cp.onResize(bounds);
	}

	public Point[] getPoints() {
		Point[] points = new Point[controlPoints.size()];
		for (int i = 0; i < controlPoints.size(); i++) {
			points[i] = controlPoints.get(i).toPoint();
		}
		return points;
	}

	abstract public IGeometry getShape();

	abstract public void onDraw(GC gc);

	public void onMove(int dragPointIndex, double oldX, double oldY) {
	}

}