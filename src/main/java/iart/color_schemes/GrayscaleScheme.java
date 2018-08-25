package iart.color_schemes;

import javafx.scene.paint.Color;
import iart.draw.DrawEvent;

import java.awt.*;

/**
 * Gray scale color scheme. Used when determining the color with which to draw various objects on screen.
 */
public class GrayscaleScheme implements ColorScheme {

	@Override
	public Color getColor(DrawEvent drawEvent, Point eventLoc) {
		switch (drawEvent) {
			case MOUSE_MOVE:
				return Color.BLACK;
			case KEYSTROKE:
			case LMOUSE_PRESS:
				return Color.gray(Math.random() / 2.5, Math.random() / 2 + 0.5);
			case MOVE_INNER_CIRCLE:
			case MOVE_OUTER_CIRCLE:
				double color = Math.random() / 4 + 0.75;
				return Color.gray(color, color);
			case BACKGROUND:
				return Color.WHITE;
			default:
				return Color.BLACK;
		}
	}

	@Override
	public void unregisterColorScheme() {
	}
}
