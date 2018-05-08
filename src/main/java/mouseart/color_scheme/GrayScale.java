package mouseart.color_scheme;

import javafx.scene.paint.Color;
import mouseart.DrawEvent;

public class GrayScale implements ColorScheme {
	public Color getColor(DrawEvent drawEvent) {
		switch (drawEvent) {
			case LMOUSE_PRESS:
				return Color.gray(Math.random() / 2.5, Math.random() / 2 + 0.5);
			case MOVE_INNER_CIRCLE:
				return Color.BLACK;
			case MOVE_OUTER_CIRCLE:
				return Color.gray(Math.random() / 4 + 0.75, Math.random() / 4);
			case LINE:
				return Color.BLACK;
			default:
				return Color.BLACK;
		}
	}
}
