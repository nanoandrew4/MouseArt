package mouseart.color_scheme;

import javafx.scene.paint.Color;
import mouseart.DrawEvent;

import java.util.ArrayList;

public class RGBScale implements ColorScheme {

	private static ArrayList<Color> colors = new ArrayList<>();
	private static long start = System.currentTimeMillis();

	static {
		for (int g = 0; g < 256; g++) colors.add(Color.rgb(255, g, 0));
		for (int b = 0; b < 256; b++) colors.add(Color.rgb(0, 255, b));
		for (int r = 0; r < 256; r++) colors.add(Color.rgb(r, 0, 255));
	}

	@Override
	public Color getColor(DrawEvent geom) {
		switch (geom) {
			case LINE:
				return colors.get((int) (System.currentTimeMillis() - start) % colors.size());
			case LMOUSE_PRESS:
				return Color.BLACK;
			case MOVE_INNER_CIRCLE:
				return Color.BLACK;
			case MOVE_OUTER_CIRCLE:
				return Color.BLACK;
			default:
				return Color.BLACK;
		}
	}
}
