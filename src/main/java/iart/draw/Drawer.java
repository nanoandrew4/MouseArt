package iart.draw;

import iart.Main;
import iart.recorder.Recorder;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Draws objects to the canvas as required by the listener classes. Once the drawing has been performed, the preview
 * may be updated to reflect the latest draw operations.
 */
public class Drawer {
	private Main main;

	private GraphicsContext gc;
	private Point2D point = new Point2D.Double();

	/**
	 * Sets up the drawer to be able to draw on the specified canvas.
	 *
	 * @param main Main class that instantiated this class
	 * @param gc   GraphicsContext of the canvas on which to draw
	 */
	public Drawer(Main main, GraphicsContext gc) {
		this.main = main;
		this.gc = gc;
	}

	private void scaleDrawPoints(Point... ps) {
		for (Point p : ps) {
			p.x *= Recorder.resMultiplier;
			p.y *= Recorder.resMultiplier;
		}
	}

	/**
	 * Draws a line on the canvas.
	 *
	 * @param start Line start coordinate
	 * @param end   Line end coordinate
	 */
	public void drawLine(Point start, Point end) {
		scaleDrawPoints(start, end);

		point.setLocation(start);

		gc.setStroke(Recorder.colorScheme.getColor(DrawEvent.MOUSE_MOVE, point));
		gc.setLineWidth(1);
		gc.strokeLine(start.x, start.y, end.x, end.y);
		main.refreshPreview();
	}

	/**
	 * Draws a circle on the canvas.
	 *
	 * @param drawEvent Determinant of color through use of color palette, depending on the figure being drawn
	 * @param center    Coordinate on canvas which will be the circles centre
	 * @param radius    Radius of the circle
	 */
	public void drawCircle(DrawEvent drawEvent, Point center, double radius) {
		scaleDrawPoints(center);
		radius *= Recorder.resMultiplier;

		point.setLocation(center);

		if (drawEvent == DrawEvent.MOVE_OUTER_CIRCLE) {
			gc.setStroke(Recorder.colorScheme.getColor(drawEvent, point));
			gc.strokeArc(center.x - radius / 2d, center.y - radius / 2d, radius, radius, 0, 360, ArcType.OPEN);
		} else {
			gc.setFill(Recorder.colorScheme.getColor(drawEvent, point));
			gc.fillArc(center.x - radius / 2d, center.y - radius / 2d, radius, radius, 0, 360, ArcType.ROUND);
		}

		main.refreshPreview();
	}

	/**
	 * Draws a square on the canvas.
	 *
	 * @param topLeft Top left coordinate on canvas where the square should be drawn
	 * @param width   Width of the square (of one of the sides)
	 */
	public void drawSquare(Point topLeft, double width) {
		scaleDrawPoints(topLeft);
		width *= Recorder.resMultiplier;

		point.setLocation(topLeft);

		gc.setStroke(Recorder.colorScheme.getColor(DrawEvent.KEYSTROKE, point));
		gc.strokeRect(topLeft.x, topLeft.y, width, width);
		main.refreshPreview();
	}
}
