package iart.color_scheme;

import iart.draw.DrawEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.HashMap;

/**
 * Determines what color each object in DrawEvent should be given, so that other color schemes can easily be added
 * and selected.
 */
public interface ColorScheme {
	/**
	 * Contains all ColorScheme implementations that were loaded by Main.loadColorSchemes(). Useful for swapping
	 * between color schemes, and easily implementing new ones.
	 */
	HashMap<String, ColorScheme> colorSchemes = new HashMap<>();

	/**
	 * Returns a color depending on the shape being drawn and the position of the mouse on the screen(s).
	 *
	 * @param drawEvent Shape being drawn
	 * @param eventLoc  Location of the mouse when the draw event was triggered
	 * @return Color to use when drawing the shape specified by drawEvent
	 */
	Color getColor(DrawEvent drawEvent, Point eventLoc);
}
