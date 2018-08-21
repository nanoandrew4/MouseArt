package iart.color_scheme;

import iart.DrawEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.HashMap;

/**
 * Determines what color each object in DrawEvent should be given, so that other color schemes can easily be added
 * and selected.
 */
public interface ColorScheme {
	/**
	 * TODO
	 */
	HashMap<String, ColorScheme> colorSchemes = new HashMap<>();

	/**
	 *
	 * @param geom
	 * @param eventLoc
	 * @return
	 */
	Color getColor(DrawEvent geom, Point eventLoc);
}
