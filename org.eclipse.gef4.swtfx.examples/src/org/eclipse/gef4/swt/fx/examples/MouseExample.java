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
package org.eclipse.gef4.swt.fx.examples;

import org.eclipse.gef4.geometry.euclidean.Angle;
import org.eclipse.gef4.geometry.planar.AffineTransform;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.swtfx.ControlNode;
import org.eclipse.gef4.swtfx.INode;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.gef4.swtfx.ShapeFigure;
import org.eclipse.gef4.swtfx.event.ActionEvent;
import org.eclipse.gef4.swtfx.event.IEventHandler;
import org.eclipse.gef4.swtfx.event.MouseEvent;
import org.eclipse.gef4.swtfx.gc.GraphicsContext;
import org.eclipse.gef4.swtfx.gc.LineCap;
import org.eclipse.gef4.swtfx.gc.RgbaColor;
import org.eclipse.gef4.swtfx.layout.Pane;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class MouseExample implements IExample {

	static class NodeDragger {
		private boolean dragging;
		private Point offset;

		public NodeDragger(final INode node) {
			node.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							// System.out.println("drag start");
							node.requestFocus();
							dragging = true;
							offset = new Point(e.getX(), e.getY());
							if (node instanceof ShapeFigure) {
								Point parentOut = new Point();
								node.localToParent(offset, parentOut);

								// System.out.println("trafo correct? "
								// + offset.equals(node
								// .parentToLocal(parentOut)));

								// System.out.println(((ShapeFigure) node)
								// .getShape() + " picked at " + parentOut);
							}
						}
					});
			node.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							// System.out.println("drag end");
							dragging = false;
						}
					});
			node.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							if (dragging) {
								Point cursor = new Point(e.getX(), e.getY());
								node.localToParent(cursor, cursor);
								node.relocate(cursor.x - offset.x, cursor.y
										- offset.y);
								node.getScene().refreshVisuals();
							}
						}
					});
		}
	}

	public class ParentTransformer {
		private boolean dragging = false;
		private Point start;
		private AffineTransform startTx;

		public ParentTransformer(final IParent parent) {
			final AffineTransform tx = new AffineTransform();
			parent.getTransforms().add(tx);

			parent.addEventHandler(MouseEvent.MOUSE_PRESSED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							// if (event.getButton() == 1) {
							// System.out.println("mouse pointer at "
							// + event.getX() + ", " + event.getY());
							// }
							if (event.getButton() == 3) {
								dragging = true;
								start = tx.getTransformed(new Point(event
										.getX(), event.getY()));
								startTx = tx.getCopy();
							}
						}
					});
			parent.addEventHandler(MouseEvent.MOUSE_RELEASED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							dragging = false;
						}
					});
			parent.addEventHandler(MouseEvent.MOUSE_MOVED,
					new IEventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							// TODO: simplify transformation calculation
							if (dragging) {
								double x = event.getX();
								double y = event.getY();

								Point transformed = tx
										.getTransformed(new Point(x, y));
								x = transformed.x;
								y = transformed.y;

								double angleDeg = y - start.y;
								double zoom = 1 + Math.abs(x - start.x) / 100;
								if (x < start.x) {
									zoom = 1 / zoom;
								}

								AffineTransform newTx = new AffineTransform()
										.translate(start.x, start.y)
										.rotate(Angle.fromDeg(angleDeg).rad())
										.scale(zoom, zoom)
										.translate(-start.x, -start.y)
										.concatenate(startTx);

								tx.setTransform(newTx);

								// System.out.println("new transform: " + tx);

								parent.getScene().refreshVisuals();
							}
						}
					});
		}
	}

	public static void main(String[] args) {
		new Example(new MouseExample());
	}

	private static ShapeFigure shape(IShape shape, RgbaColor color) {
		ShapeFigure figure = new ShapeFigure(shape) {
			{
				// TODO: implement StrokeType dependent drawing
				// setStrokeType(StrokeType.INSIDE);
			}

			@Override
			public void doPaint(GraphicsContext g) {
				if (isFocused()) {
					g.setDashes(20, 20);
					g.setLineWidth(5);
					g.setLineCap(LineCap.ROUND);
					g.setStroke(new RgbaColor());
				}
				super.doPaint(g);
			}
		};
		figure.getPaintStateByReference().getFillByReference().setColor(color);
		return figure;
	}

	private ShapeFigure rectFigure = shape(new Rectangle(0, 0, 200, 100),
			new RgbaColor(0, 64, 255, 255));
	private ShapeFigure ovalFigure = shape(new Ellipse(0, 0, 100, 200),
			new RgbaColor(255, 64, 0, 255));
	private IParent root;
	private ControlNode<Button> resetButton;
	private ControlNode<Button> quitButton;

	@Override
	public void addUi(final IParent realRoot) {
		final Pane root = new Pane();
		realRoot.addChildNodes(root);

		this.root = root;
		root.addChildNodes(rectFigure, ovalFigure);
		new NodeDragger(rectFigure);
		new NodeDragger(ovalFigure);
		new ParentTransformer(root);

		resetButton = new ControlNode<Button>(new Button(root.getScene(),
				SWT.PUSH));
		resetButton.getControl().setText("Reset");
		resetButton.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						resetFigures();
					}
				});

		quitButton = new ControlNode<Button>(new Button(root.getScene(),
				SWT.PUSH));
		quitButton.getControl().setText("Quit");
		quitButton.addEventHandler(ActionEvent.SELECTION,
				new IEventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Composite compo = root.getScene();
						while (compo != null && !(compo instanceof Shell)) {
							compo = compo.getParent();
						}
						if (compo instanceof Shell) {
							((Shell) compo).close();
						}
					}
				});

		new NodeDragger(resetButton);
		new NodeDragger(quitButton);

		root.addChildNodes(resetButton, quitButton);
		resetFigures();
	}

	@Override
	public int getHeight() {
		return 480;
	}

	@Override
	public String getTitle() {
		return "Mouse Example";
	}

	@Override
	public int getWidth() {
		return 640;
	}

	private void resetFigures() {
		rectFigure.relocate(0, 0);
		ovalFigure.relocate(0, 0);
		resetButton.relocate(20, 300);
		quitButton.relocate(300, 300);
		if (root.getTransforms().size() > 0) {
			root.getTransforms().get(0).setToIdentity();
		}
		root.getScene().refreshVisuals();
	}

}
