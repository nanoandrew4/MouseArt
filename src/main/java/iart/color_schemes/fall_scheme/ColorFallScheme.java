package iart.color_schemes.fall_scheme;

import iart.Main;
import iart.color_schemes.ColorScheme;
import iart.draw.DrawEvent;
import javafx.scene.paint.Color;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */
public class ColorFallScheme implements ColorScheme, NativeMouseWheelListener {
	private int currHue;

	boolean vertical;
	boolean grayscale;

	@Override
	public void registerSuperScheme() {
		String[] schemes = {"fall_scheme.VertColorFall", "fall_scheme.HorColorFall", "fall_scheme.VertGrayscaleFall",
							"fall_scheme.HorGrayscaleFall"};
		ColorScheme.superSchemes.putIfAbsent("Fall", new ArrayList<>(Arrays.asList(schemes)));
	}

	@Override
	public Color getColor(DrawEvent drawEvent, Point eventLoc) {
		double ratio = 0d;
		if (eventLoc != null)
			ratio = vertical ? (double) eventLoc.y / Main.screenHeight : (double) eventLoc.x / Main.screenWidth;
		switch (drawEvent) {
			case MOUSE_MOVE:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : 1d - ratio,
								 grayscale ? ratio : 1d,
								 1
				);
			case KEYSTROKE:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : Math.min(1d - ratio + (Math.random() / 10d), 1d),
								 grayscale ? Math.min(ratio + (Math.random() / 10d), 1d) : 1d,
								 Math.random()
				);
			case LMOUSE_PRESS:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : Math.max(1d - ratio - (Math.random() / 10d), 0d),
								 grayscale ? Math.max(ratio - (Math.random() / 10d), 0d) : 1d,
								 Math.random()
				);
			case MOVE_OUTER_CIRCLE:
				return grayscale ? Color.BLACK : Color.WHITE;
			case MOVE_INNER_CIRCLE:
			case BACKGROUND:
				return grayscale ? Color.WHITE : Color.BLACK;
			default:
				return Color.WHITE;
		}
	}

	@Override
	public void startColorScheme() {
		GlobalScreen.addNativeMouseWheelListener(this);
		currHue = (int) (Math.random() * 255);
	}

	@Override
	public void stopColorScheme() {
		GlobalScreen.removeNativeMouseWheelListener(this);
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
		currHue += nativeMouseWheelEvent.getWheelRotation();

		if (currHue >= 360) currHue -= 360;
		else if (currHue < 0) currHue += 360;
	}
}
