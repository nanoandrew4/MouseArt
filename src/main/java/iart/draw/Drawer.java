package iart.draw;

import iart.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

import java.awt.*;

/**
 * Draws objects to the canvas as required by the listener classes. Once the drawing has been performed, the preview
 * may be updated to reflect the latest draw operations.
 */
public class Drawer {
	private Main main;

	private GraphicsContext gc;
	private Point point;

	/**
	 * Sets up the drawer to be able to draw on the specified canvas.
	 *
	 * @param main Main class that instantiated this class
	 * @param gc   GraphicsContext of the canvas on which to draw
	 */
	public Drawer(Main main, GraphicsContext gc) {
		this.main = main;
		this.gc = gc;

		point = new Point();
	}

	/**
	 * Draws a line on the canvas.
	 *
	 * @param startX Line start coordinate on the x axis
	 * @param startY Line start coordinate on the y axis
	 * @param endX   Line end coordinate on the x axis
	 * @param endY   Line end coordinate on the y axis
	 */
	public void drawLine(int startX, int startY, int endX, int endY) {
		point.x = startX;
		point.y = startY;

		gc.setStroke(Main.colorScheme.getColor(DrawEvent.LINE, point));
		gc.setLineWidth(1);
		gc.strokeLine(startX, startY, endX, endY);
		main.refreshPreview();
	}

	/**
	 * Draws a circle on the canvas.
	 *
	 * @param drawEvent Determinant of color through use of color palette, depending on the figure being drawn
	 * @param centreX   Circle center on the x axis
	 * @param centreY   Circle center on the y axis
	 * @param radius    Radius of the circle
	 */
	public void drawCircle(DrawEvent drawEvent, int centreX, int centreY, int radius) {
		point.x = centreX;
		point.y = centreY;

		gc.setFill(Main.colorScheme.getColor(drawEvent, point));
		gc.fillArc(centreX - radius / 2, centreY - radius / 2, radius, radius, 0, 360, ArcType.ROUND);
		if (drawEvent == DrawEvent.MOVE_OUTER_CIRCLE)
			gc.strokeArc(centreX - radius / 2, centreY - radius / 2, radius, radius, 0, 360, ArcType.OPEN);
		main.refreshPreview();
	}

	/**
	 * Draws a square on the canvas.
	 *
	 * @param topLeftX Top left x coordinate on which to place the square
	 * @param topLeftY Top left y coordinate on which to place the square
	 * @param width    Width of the square (of one of the sides)
	 */
	public void drawSquare(int topLeftX, int topLeftY, int width) {
		point.x = topLeftX;
		point.y = topLeftY;

		gc.setStroke(Main.colorScheme.getColor(DrawEvent.SQUARE, point));
		gc.strokeRect(topLeftX, topLeftY, width, width);
		main.refreshPreview();
	}
}
