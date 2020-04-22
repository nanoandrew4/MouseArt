package iart.color_schemes.wheel_scheme;

import iart.GlobalVariables;
import iart.color_schemes.ColorScheme;
import iart.draw.DrawEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This classes subclasses take advantage of the HSB color model to create a wheel of color. In essence, the
 * effect created is as if there was a wheel of color (such as the ones you find in color pickers) behind a layer of
 * black, and the mouse was scratching the black off to reveal the color behind it, or the grayscale in case a
 * grayscale subscheme was used.
 */
public class WheelScheme implements ColorScheme {
	boolean grayscale;
	boolean inverted;

	private long startTime;

	@Override
	public void registerSuperScheme() {
		String[] schemes = {"wheel_scheme.ColorWheel", "wheel_scheme.GrayscaleWheel", "wheel_scheme.InvColorWheel",
							"wheel_scheme.InvGrayscaleWheel"};
		ColorScheme.superSchemes.putIfAbsent("Wheel", new ArrayList<>(Arrays.asList(schemes)));
	}

	@Override
	public Color getColor(DrawEvent drawEvent, Point2D eventLoc) {
		switch (drawEvent) {
			case MOUSE_MOVE:
			case KEYSTROKE:
			case LMOUSE_PRESS:
				Point centrePoint = new Point((int) GlobalVariables.getVirtualScreenWidth() / 2, (int) GlobalVariables.getVirtualScreenHeight() / 2);

				double angleRad = Math.atan((centrePoint.getY() - eventLoc.getY()) / (eventLoc.getX() -
																					  centrePoint.getX()));
				double angleDeg = Math.toDegrees(angleRad);
				if (eventLoc.getX() < centrePoint.getX()) // Convert to 0->360, since Math lib returns -90->90 range
					angleDeg += 180;
				else if (eventLoc.getY() > centrePoint.getY()) // Convert to 0->360 range
					angleDeg += 360;

				double distFromCentre = Math.sqrt(
						(eventLoc.getY() - centrePoint.getY()) * (eventLoc.getY() - centrePoint.getY()) +
						(eventLoc.getX() - centrePoint.getX()) * (eventLoc.getX() - centrePoint.getX())
				);
				double distToBorder = distToBorder(eventLoc.getX(), eventLoc.getY(), centrePoint);
				// Min/Max removes rounding errors, if there are any
				double distToBorderRatio = Math.min(Math.max(distFromCentre / distToBorder, 0d), 1d);

				return getSchemeColor(drawEvent, angleDeg, distToBorderRatio, distFromCentre);
			case MOVE_OUTER_CIRCLE:
				return grayscale ? Color.BLACK : Color.WHITE;
			case MOVE_INNER_CIRCLE:
			case BACKGROUND:
				return grayscale ? Color.WHITE : Color.BLACK;
			default:
				return Color.BLACK;
		}
	}

	/**
	 * Based on the wheel scheme grayscale and inverted settings, this method returns a color in the using the HSB
	 * color model. Grayscale schemes will not have any color, as the name implies, and will be black, white and all
	 * grays in between. Inverted wheel schemes will have the point of maximum color vividness at the centre of the
	 * screen, instead of at the edges of the screen.
	 *
	 * @param drawEvent         DrawEvent that is being processed
	 * @param angle             Angle (in degrees) at which the cursor is located from the centre of the screen
	 * @param distToBorderRatio Ratio between the distance from the centre of the screen to the mouse pointer, and
	 *                          from the centre of the screen to the border of the screen, with both distances
	 *                          being at the same angle passed as a parameter to this method.
	 * @param distFromCentre    Distance of the mouse cursor from the centre of the screen
	 * @return Color to use when drawing
	 */
	private Color getSchemeColor(DrawEvent drawEvent, double angle, double distToBorderRatio, double distFromCentre) {
		return Color.hsb(
				grayscale ? 0 : ((angle + ((System.currentTimeMillis() - startTime) / 60000d)
								  + (drawEvent == DrawEvent.MOUSE_MOVE ? 0 : 30)) % 360),
				grayscale ? 0 : (inverted ? 1 - distToBorderRatio : distToBorderRatio),
				grayscale ? (inverted ? (1 - ((1 - distToBorderRatio) / 2)) : (1 - distToBorderRatio) / 2)
						  : 1,
				drawEvent == DrawEvent.LMOUSE_PRESS ? getOpacity(distFromCentre) : 1
						);
	}

	@Override
	public void startColorScheme() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void stopColorScheme() {
	}

	/**
	 * Sigmoid function which returns a value between 0.33 and 1, which is used to determine the opacity of a shape.
	 * The value is calculated using the distance of shape being drawn from the centre of the screen.
	 *
	 * @param distFromCentre Distance from the centre of the screen to the object being drawn
	 * @return Value between 0.33-1
	 */
	private double getOpacity(double distFromCentre) {
		return 1 / (1d + 2 * Math.exp((-1 / (GlobalVariables.getVirtualScreenHeight() / 2d)) * distFromCentre));
	}

	/**
	 * Calculates the distance from the centre of the screen to the border of the screen, using the slope of the line
	 * from the centre of the screen to the mouse position.
	 *
	 * @param px          Mouse x coordinate on screen
	 * @param py          Mouse y coordinate on screen
	 * @param centrePoint Coordinates of the centre of the screen(s)
	 * @return Distance from the centre of the screen to the border
	 */
	private double distToBorder(double px, double py, Point centrePoint) {
		double dy = py - centrePoint.getY();
		double dx = centrePoint.getX() - px;

		double slope = dy / dx;
		double borderX, borderY;

		double diagScreenSlope = GlobalVariables.getVirtualScreenWidth() / GlobalVariables.getVirtualScreenHeight();

		if (slope > diagScreenSlope || slope < -diagScreenSlope) {
			borderX = ((GlobalVariables.getVirtualScreenWidth() / 2d) * (py > 0 ? 1 : -1) - dy) / slope + dx;
			borderY = (borderX * dy) / dx;
		} else {
			borderY = slope * ((GlobalVariables.getVirtualScreenWidth() / 2d) * (px > 0 ? 1 : -1) - dx) + dy;
			borderX = (borderY * dx) / dy;
		}

		return Math.sqrt(borderX * borderX + borderY * borderY);
	}
}
