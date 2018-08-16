package iart.color_scheme;

import javafx.scene.paint.Color;
import iart.DrawEvent;

/**
 * Determines what color each object in DrawEvent should be given, so that other color schemes can easily be added.
 */
public interface ColorScheme {
	Color getColor(DrawEvent geom);
}
