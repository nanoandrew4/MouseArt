package iart.color_scheme;

import iart.draw.DrawEvent;
import iart.Main;
import javafx.scene.paint.Color;

import java.awt.*;

/**
 * Color scheme that makes the canvas emulate a color wheel. The cursor position is used to calculate the HSV color
 * that the shape should be drawn with.
 */
public class ColorWheelScheme implements ColorScheme {
	private static Point centrePoint = new Point(Main.screenWidth / 2, Main.screenHeight / 2);
	private static final double diagScreenSlope = Main.screenHeight / (double) Main.screenWidth;

	private boolean grayscale = false; // TODO: HSV grayscale pending

	@Override
	public Color getColor(DrawEvent drawEvent, Point eventLoc) {
		switch (drawEvent) {
			case LINE:
			case LMOUSE_PRESS:
				double angleRad = Math.atan((double) (centrePoint.y - eventLoc.y) / (eventLoc.x - centrePoint.x));
				double angleDeg = Math.toDegrees(angleRad);
				if (eventLoc.x < centrePoint.x) // Convert to 0->360 range, since Math lib returns -90->90 range
					angleDeg += 180;
				else if (eventLoc.y > centrePoint.y) // Convert to 0->360 range
					angleDeg += 360;

				double distFromCentre = Math.sqrt((eventLoc.y - centrePoint.y) * (eventLoc.y - centrePoint.y) +
												  (eventLoc.x - centrePoint.x) * (eventLoc.x - centrePoint.x));
				double distToBorder = distToBorder(eventLoc.x - centrePoint.x, centrePoint.y - eventLoc.y);
				double distToBorderRatio = Math.min(Math.max(distFromCentre / distToBorder, 0d), 1d);

				return Color.hsb(angleDeg + (drawEvent == DrawEvent.LINE ? 0 : 30),
								 1 - 1 / (3 * distToBorderRatio + 1d), 1, 1);
			case MOVE_INNER_CIRCLE:
			case MOVE_OUTER_CIRCLE:
				double color = Math.random() / 4;
				return Color.gray(color, color);
			case BACKGROUND:
				return Color.BLACK;
			default:
				return Color.BLACK;
		}
	}

	/**
	 * Calculates the distance from the centre of the screen to the border of the screen, using the slope of the line
	 * from the centre of the screen to the cursor position.
	 *
	 * @param x Mouse x coordinate on screen
	 * @param y Mouse y coordinate on screen
	 * @return Distance from the centre of the screen to the border
	 */
	private double distToBorder(double x, double y) {
		double slope = y / x;
		double borderX, borderY;
		if (slope > diagScreenSlope || slope < -diagScreenSlope) {
			borderX = (Main.screenHeight / 2 * (y > 0 ? 1 : -1) + y) / slope + x;
			borderY = (borderX * y) / x;
		} else {
			borderY = slope * (Main.screenWidth / 2 * (x > 0 ? 1 : -1) - x) + y;
			borderX = (borderY * x) / y;
		}

		return Math.sqrt(borderX * borderX + borderY * borderY);
	}
}
