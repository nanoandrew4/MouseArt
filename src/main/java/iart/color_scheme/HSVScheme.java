package iart.color_scheme;

import iart.DrawEvent;
import iart.Main;
import javafx.scene.paint.Color;

import java.awt.*;

/**
 * TODO
 */
public class HSVScheme implements ColorScheme {
	private Point centrePoint = new Point(Main.screenWidth / 2, Main.screenHeight / 2);

	static {
		colorSchemes.put("HSV Scheme", new HSVScheme());
	}

	@Override
	public Color getColor(DrawEvent drawEvent, Point eventLoc) {
		switch (drawEvent) {
			case LINE:
				double angle = Math.tan((eventLoc.x - centrePoint.x) / (eventLoc.y - centrePoint.y));
				double distFromCentre = Math.sqrt((eventLoc.y - centrePoint.y) * (eventLoc.y - centrePoint.y) +
												  (eventLoc.x - centrePoint.x) * (eventLoc.x - centrePoint.x));
				double maxDistAtAngle = Math.abs(eventLoc.x - centrePoint.x) / Math.abs(Math.sin(angle - Math.PI));
				return Color.hsb(Math.toDegrees(angle), distFromCentre / maxDistAtAngle,
								 distFromCentre / maxDistAtAngle, distFromCentre / maxDistAtAngle);
			case LMOUSE_PRESS:
				return Color.gray(Math.random() / 2.5, Math.random() / 2 + 0.5);
			case MOVE_INNER_CIRCLE:
			case MOVE_OUTER_CIRCLE:
				double color = Math.random() / 4 + 0.75;
				return Color.gray(color, color);
			case BACKGROUND:
				return Color.BLACK;
			default:
				return Color.BLACK;
		}
	}
}
