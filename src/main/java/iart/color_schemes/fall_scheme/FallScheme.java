package iart.color_schemes.fall_scheme;

import iart.GlobalVariables;
import iart.color_schemes.ColorScheme;
import iart.draw.DrawEvent;
import javafx.scene.paint.Color;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class and its subclasses implement a color scheme that creates the following effect. The topmost or leftmost
 * (depending on orientation) part of the screen has the highest saturation level, meaning the color is most intense.
 * The opposite side has the lowest saturation level, and there is a gradient between both. For the grayscale
 * implementations, the topmost or leftmost part of the screen is black, and the opposite is white, with a gradient
 * between them too. The mouse wheel changes the hue of the color being drawn, which means grayscale implementations
 * do not need the mouse wheel listener.
 */
public class FallScheme implements ColorScheme, NativeMouseWheelListener {
	private int currHue = (int) (Math.random() * 255);

	boolean vertical; // Orientation of the color fall
	boolean grayscale; // True if grayscale fall should be used, false if color fall should be used

	@Override
	public void registerSuperScheme() {
		String[] schemes = {"fall_scheme.VertColorFall", "fall_scheme.HorColorFall", "fall_scheme.VertGrayscaleFall",
							"fall_scheme.HorGrayscaleFall"};
		ColorScheme.superSchemes.putIfAbsent("Fall", new ArrayList<>(Arrays.asList(schemes)));
	}

	@Override
	public Color getColor(DrawEvent drawEvent, Point2D eventLoc) {
		double locToEdgeRatio = 0d;
		if (eventLoc != null)
			locToEdgeRatio = vertical ? eventLoc.getY() / GlobalVariables.getVirtualScreenHeight() : eventLoc.getX() / GlobalVariables.getVirtualScreenWidth();
		switch (drawEvent) {
			case MOUSE_MOVE:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : 1d - locToEdgeRatio,
								 grayscale ? locToEdgeRatio : 1d,
								 1
								);
			case KEYSTROKE:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : Math.min(1d - locToEdgeRatio + (Math.random() / 10d), 1d),
								 grayscale ? Math.min(locToEdgeRatio + (Math.random() / 10d), 1d) : 1d,
								 Math.random()
								);
			case LMOUSE_PRESS:
				return Color.hsb(grayscale ? 0d : currHue,
								 grayscale ? 0d : Math.max(1d - locToEdgeRatio - (Math.random() / 10d), 0d),
								 grayscale ? Math.max(locToEdgeRatio - (Math.random() / 10d), 0d) : 1d,
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
