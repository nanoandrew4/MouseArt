package iart.color_schemes;

import iart.Main;
import iart.draw.DrawEvent;
import javafx.scene.paint.Color;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseWheelEvent;
import org.jnativehook.mouse.NativeMouseWheelListener;

import java.awt.*;

public class ColorFallScheme implements ColorScheme, NativeMouseWheelListener {
	private int currHue = (int) (Math.random() * 255);

	public ColorFallScheme() {
		GlobalScreen.addNativeMouseWheelListener(this);
	}

	@Override
	public Color getColor(DrawEvent drawEvent, Point eventLoc) {
		switch (drawEvent) {
			case MOUSE_MOVE:
				return Color.hsb(currHue, 1 - eventLoc.y / Main.screenHeight, 1, 1);
			case KEYSTROKE:
				return Color.hsb(currHue, Math.min(1 - (eventLoc.y / Main.screenHeight) + (Math.random() / 10d), 0d),
								 1, Math.random());
			case LMOUSE_PRESS:
				return Color.hsb(currHue, Math.max(1 - (eventLoc.y / Main.screenHeight) - (Math.random() / 10d), 1d),
								 1, Math.random());
			case MOVE_INNER_CIRCLE:
				return Color.WHITE;
			case MOVE_OUTER_CIRCLE:
				return Color.BLACK;
			case BACKGROUND:
			default:
				return Color.WHITE;
		}
	}

	@Override
	public void unregisterColorScheme() {
		GlobalScreen.removeNativeMouseWheelListener(this);
	}

	@Override
	public void nativeMouseWheelMoved(NativeMouseWheelEvent nativeMouseWheelEvent) {
		currHue += nativeMouseWheelEvent.getWheelRotation();

		if (currHue >= 360) currHue -= 360;
		else if (currHue < 0) currHue += 360;
	}
}
