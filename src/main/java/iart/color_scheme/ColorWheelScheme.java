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

	boolean grayscale = false;

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
				double distToBorder = distToBorder(eventLoc.x, eventLoc.y);
				double distToBorderRatio = Math.min(Math.max(distFromCentre / distToBorder, 0d), 1d);

				if (!grayscale)
					return Color.hsb((angleDeg + (drawEvent == DrawEvent.LINE ? 0 : 45) % 360),
									 distToBorderRatio, 1,
									 drawEvent == DrawEvent.LINE ? 1 : getOpacity(distFromCentre));
				else
					return Color.hsb(0, 0, (1 - distToBorderRatio) / 2,
									 drawEvent == DrawEvent.LINE ? 1 : getOpacity(distFromCentre));
			case MOVE_INNER_CIRCLE:
			case MOVE_OUTER_CIRCLE:
				double color = Math.random() / 4;
				return Color.gray(color, color);
			case BACKGROUND:
				return grayscale ? Color.WHITE : Color.BLACK;
			default:
				return Color.BLACK;
		}
	}

	private double getOpacity(double distFromCentre) {
		return 1 / (1d + Math.exp((-1 / (Main.screenHeight / 3d)) * distFromCentre));
	}

	/**
	 * Calculates the distance from the centre of the screen to the border of the screen, using the slope of the line
	 * from the centre of the screen to the mouse position.
	 *
	 * @param px Mouse x coordinate on screen
	 * @param py Mouse y coordinate on screen
	 * @return Distance from the centre of the screen to the border
	 */
	private double distToBorder(double px, double py) {
		double slope = (py - centrePoint.y) / (centrePoint.x - px);
		double borderX, borderY;

		double dy = py - centrePoint.y;
		double dx = centrePoint.x - px;

		if (slope > diagScreenSlope || slope < -diagScreenSlope) {
			borderX = ((Main.screenHeight / 2) * (py > 0 ? 1 : -1) - dy) / slope + dx;
			borderY = (borderX * dy) / dx;
		} else {
			borderY = slope * ((Main.screenWidth / 2) * (px > 0 ? 1 : -1) - dx) + dy;
			borderX = (borderY * dx) / dy;
		}

		return Math.sqrt(borderX * borderX + borderY * borderY);
	}
}
