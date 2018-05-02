package mouseart;

import javafx.application.Platform;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;

public class MouseHook implements NativeMouseInputListener {
	private MouseArt mArt;

	private Point prevLocation;
	private long lastMove, diff;
	private boolean mousePressed;

	MouseHook(MouseArt mArt) {
		this.mArt = mArt;
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
		if (mousePressed)
			return;
		mousePressed = true;
		drawCircle(prevLocation.x, prevLocation.y, MouseArt.rand.nextInt(50) + 25, true);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		mousePressed = false;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		Point location = nativeMouseEvent.getPoint();

		/*
		 * If the mouse has moved, draw a line between previous position and current position.
		 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
		 * cube root of the time elapsed until the mouse was moved again.
		 */
		if (MouseArt.state == mouseart.State.RECORDING) {
			if (!prevLocation.equals(location)) {
				if ((diff = System.currentTimeMillis() - lastMove) > 3000) {
					int radius = (int) diff;
					drawCircle(prevLocation.x, prevLocation.y, radius, false);
					drawCircle(prevLocation.x, prevLocation.y, radius / 10, true);
				}
				lastMove = System.currentTimeMillis();
				drawLine(prevLocation.x, prevLocation.y, location.x, location.y);
			}
		}
		prevLocation = location;
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
	}

	public void prepForRecording() {
		prevLocation = MouseInfo.getPointerInfo().getLocation();
		lastMove = System.currentTimeMillis();
	}

	private void drawLine(int startX, int startY, int endX, int endY) {
		Platform.runLater(() -> mArt.addLineOp(startX, startY, endX, endY));
	}

	private void drawCircle(int centreX, int centreY, int radius, boolean fill) {
		Platform.runLater(() -> mArt.addCircleOp(centreX, centreY, radius, fill));
	}
}
