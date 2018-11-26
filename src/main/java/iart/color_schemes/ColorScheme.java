package iart.color_schemes;

import iart.draw.DrawEvent;
import javafx.scene.paint.Color;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Specifies what color should be used for each of the DrawEvents, given a position on screens(s).
 */
public interface ColorScheme {
	/**
	 * Color schemes to be loaded for use. Must follow class name, minus the "Scheme" part of the class name, that gets
	 * added in. Only add lone schemes and top level super schemes, lower level super schemes will be added in
	 * automatically.
	 */
	String[] topLevelSchemes = {"Grayscale", "Rainbow", "Fall", "Wheel"};

	/**
	 * Contains all super schemes, which are classes that inherit ColorScheme, and have any number of subschemes.
	 * Any subschemes of the superschemes must include any packages it is in, below the color_schemes package.
	 * Example: fall_scheme.HorColorFallScheme
	 */
	HashMap<String, ArrayList<String>> superSchemes = new HashMap<>();

	/**
	 * Contains all ColorScheme implementations that were loaded by Main.loadColorSchemes(). Useful for swapping
	 * between color schemes, and easily adding new ones.
	 */
	HashMap<String, ColorScheme> colorSchemes = new HashMap<>();

	/**
	 * Registers a super scheme in the superSchemes hashmap. A super scheme is a class that implements (or inherits)
	 * ColorScheme, and that has any number of subclasses. This allows them to be put in a submenu in the UI, and
	 * keep all the schemes tidy. Any subschemes of the superschemes must include any packages it is in, below the
	 * color_schemes package. Example: fall_scheme.HorColorFallScheme, instead of simply HorColorFallScheme.
	 */
	void registerSuperScheme();

	/**
	 * Returns a color depending on the shape being drawn and a position on the screen(s).
	 *
	 * @param drawEvent Shape being drawn
	 * @param eventLoc  Location of the mouse when the draw event was triggered
	 * @return Color to use when drawing the shape specified by drawEvent
	 */
	Color getColor(DrawEvent drawEvent, Point2D eventLoc);

	/**
	 * Allows a color scheme to set itself up before it starts being used, if necessary.
	 */
	void startColorScheme();

	/**
	 * Allows the active color scheme to shut down and clean up, if necessary, before it is swapped for another color
	 * scheme.
	 */
	void stopColorScheme();
}