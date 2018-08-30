package iart.color_schemes;

import iart.draw.DrawEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Specifies what color should be used for each of the DrawEvents, given a position on screens(s).
 */
public interface ColorScheme {
	/**
	 * Color schemes to be loaded for use. Must follow class name, minus the "Scheme" part of the class name, that gets
	 * added in.
	 */
	String[] colorSchemesStr = {"Grayscale", "Rainbow", "ColorFall", "Wheel"};

	HashMap<String, ArrayList<String>> superSchemes = new HashMap<>();

	/**
	 * Contains all ColorScheme implementations that were loaded by Main.loadColorSchemes(). Useful for swapping
	 * between color schemes, and easily adding new ones.
	 */
	HashMap<String, ColorScheme> colorSchemes = new HashMap<>();

	void registerSuperScheme();

	/**
	 * Returns a color depending on the shape being drawn and a position on the screen(s).
	 *
	 * @param drawEvent Shape being drawn
	 * @param eventLoc  Location of the mouse when the draw event was triggered
	 * @return Color to use when drawing the shape specified by drawEvent
	 */
	Color getColor(DrawEvent drawEvent, Point eventLoc);

	/**
	 *
	 */
	void unregisterColorScheme();
}
