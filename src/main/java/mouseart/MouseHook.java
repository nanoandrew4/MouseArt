package mouseart;

import javafx.application.Platform;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.Point;
import java.awt.MouseInfo;

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
		drawCircle(DrawEvent.LMOUSE_PRESS, prevLocation.x, prevLocation.y, MouseArt.rand.nextInt(70) + 5);
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
					int radius = (int) Math.cbrt(diff);
					drawCircle(DrawEvent.MOVE_OUTER_CIRCLE, location.x, location.y, radius);
					drawCircle(DrawEvent.MOVE_INNER_CIRCLE, location.x, location.y, radius / 10);
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
		Platform.runLater(() -> mArt.addLineOp(DrawEvent.LINE, startX, startY, endX, endY));
	}

	private void drawCircle(DrawEvent drawEvent, int centreX, int centreY, int radius) {
		Platform.runLater(() -> mArt.addCircleOp(drawEvent, centreX, centreY, radius));
	}
}
