package mouseart.color_scheme;

import mouseart.geometry.Geometry;

import java.awt.*;

public interface ColorScheme {
	Color getColor(Geometry g, int alpha);
}
