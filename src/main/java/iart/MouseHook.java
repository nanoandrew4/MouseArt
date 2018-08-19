package iart;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.Point;
import java.awt.MouseInfo;

/**
 * Listens for mouse events and triggers draw events to create a visual representation of the users mouse movements
 * and clicks.
 */
public class MouseHook implements NativeMouseInputListener {
	private iArt iArt;

	private Point prevLocation;
	private long lastMove;
	private boolean mousePressed;

	private int mPressCircleRad;

	/**
	 * Sets up the mouse listener and registers it as a global listener. Once this constructor returns, the mouse
	 * listener is fully operational, and will start processing mouse movement/click events immediately.
	 *
	 * @param iArt         iArt instance that owns this KeyHook instance
	 * @param screenWidth  Width of the screen(s) in pixels
	 * @param screenHeight Height of the screen(s) in pixels
	 */
	MouseHook(iArt iArt, int screenWidth, int screenHeight) {
		this.iArt = iArt;
		mPressCircleRad = (screenWidth > screenHeight ? screenWidth : screenHeight) / 50;

		prevLocation = MouseInfo.getPointerInfo().getLocation();
		lastMove = System.currentTimeMillis();

		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {
		if (mousePressed)
			return;
		mousePressed = true;
		drawCircle(DrawEvent.LMOUSE_PRESS, prevLocation.x, prevLocation.y, iArt.rand.nextInt(mPressCircleRad) + 5);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		mousePressed = false;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		Point location = nativeMouseEvent.getPoint();
		long diff;

		/*
		 * If the mouse has moved, draw a line between previous position and current position.
		 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
		 * cube root of the time elapsed until the mouse was moved again.
		 */
		if (iArt.state == iart.State.RECORDING) {
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

	private void drawLine(int startX, int startY, int endX, int endY) {
		Platform.runLater(() -> iArt.drawLine(startX, startY, endX, endY));
	}

	private void drawCircle(DrawEvent drawEvent, int centreX, int centreY, int radius) {
		Platform.runLater(() -> iArt.drawCircle(drawEvent, centreX, centreY, radius));
	}
}
