package iart.color_schemes;

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
			case MOUSE_MOVE:
			case KEYSTROKE:
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
				// Min/Max removes rounding errors, if there are any
				double distToBorderRatio = Math.min(Math.max(distFromCentre / distToBorder, 0d), 1d);

				if (!grayscale)
					return Color.hsb((angleDeg + (drawEvent == DrawEvent.MOUSE_MOVE ? 0 : 30) % 360), distToBorderRatio, 1,
									 drawEvent == DrawEvent.LMOUSE_PRESS ? getOpacity(distFromCentre) : 1);
				else
					return Color.hsb(0, 0, (1 - distToBorderRatio) / 2,
									 drawEvent == DrawEvent.LMOUSE_PRESS ? getOpacity(distFromCentre) : 1);
			case MOVE_OUTER_CIRCLE:
				return grayscale ? Color.BLACK : Color.WHITE;
			case MOVE_INNER_CIRCLE:
			case BACKGROUND:
				return grayscale ? Color.WHITE : Color.BLACK;
			default:
				return Color.BLACK;
		}
	}

	@Override
	public void unregisterColorScheme() {
	}

	/**
	 * Sigmoid function which returns a value between 0.33 and 1, which is used to determine the opacity of a shape.
	 * The value is calculated using the distance of shape being drawn from the centre of the screen.
	 *
	 * @param distFromCentre Distance from the centre of the screen to the object being drawn
	 * @return Value between 0.33-1
	 */
	private double getOpacity(double distFromCentre) {
		return 1 / (1d + 2 * Math.exp((-1 / (Main.screenHeight / 2d)) * distFromCentre));
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
		double dy = py - centrePoint.y;
		double dx = centrePoint.x - px;

		double slope = dy / dx;
		double borderX, borderY;

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
