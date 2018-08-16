package iart.color_scheme;

import javafx.scene.paint.Color;
import iart.DrawEvent;

public class GrayScale implements ColorScheme {
	public Color getColor(DrawEvent drawEvent) {
		switch (drawEvent) {
			case LINE:
				return Color.BLACK;
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
}