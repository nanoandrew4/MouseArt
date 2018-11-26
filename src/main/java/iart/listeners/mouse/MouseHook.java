package iart.listeners.mouse;

import iart.recorder.Recorder;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.Main;
import iart.recorder.State;
import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import java.awt.*;
import java.util.Random;

/**
 * Listens for mouse events and triggers draw events to create a visual representation of the users mouse movements
 * and clicks.
 */
public class MouseHook implements NativeMouseInputListener {
	private Drawer drawer;

	private Random rand = new Random();
	private Point prevLocation;
	private long lastMove;
	private boolean mousePressed;

	private int mPressCircleRad;

	/**
	 * Sets up the mouse listener and registers it as a global listener. Once this constructor returns, the mouse
	 * listener is fully operational, and will start processing mouse movement/click events immediately.
	 *
	 * @param drawer       Drawer instance to draw the lines and mouse clicks with
	 * @param screenWidth  Width of the screen(s) in pixels
	 * @param screenHeight Height of the screen(s) in pixels
	 */
	public MouseHook(Drawer drawer, double screenWidth, double screenHeight) {
		this.drawer = drawer;
		mPressCircleRad = (int) (screenWidth > screenHeight ? screenWidth : screenHeight) / 50;

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
		drawCircle(DrawEvent.LMOUSE_PRESS, prevLocation, rand.nextInt(mPressCircleRad) + 5);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		mousePressed = false;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		Point location = nativeMouseEvent.getPoint();
		System.out.println(location);
		long diff;

		/*
		 * If the mouse has moved, draw a line between previous position and current position.
		 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
		 * cube root of the time elapsed until the mouse was moved again.
		 */
		if (Recorder.state == State.RECORDING) {
			if (!prevLocation.equals(location)) {
				if ((diff = System.currentTimeMillis() - lastMove) > 3000) {
					double radius = getMouseMoveRadius(diff / 1000d);
					drawCircle(DrawEvent.MOVE_OUTER_CIRCLE, location, radius);
					drawCircle(DrawEvent.MOVE_INNER_CIRCLE, location, radius / 10);
				}
				lastMove = System.currentTimeMillis();
				drawLine(prevLocation, location);
			}
		}
		prevLocation = location;
	}

	/**
	 * Returns a radius for the circle to be drawn when the mouse is moved, after being stopped for a bit. The formula
	 * is a modified sigmoid function, chosen because I think it works well for this purpose. It caps at a quarter the
	 * shortest screen dimension, so that the circles can not get infinitely big. Completely made up, the values were
	 * toyed with until a good result was given.
	 *
	 * @param diffSecs Time between when the mouse stopped moving and when it started moving again
	 * @return Radius to use when drawing the mouse move circle
	 */
	private static double getMouseMoveRadius(double diffSecs) {
		return ((Main.screenWidth > Main.screenHeight ? Main.screenHeight : Main.screenHeight) / 4d) /
			   (1d + 35d * Math.exp(-0.001d * diffSecs)) - 15d;
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
	}

	private void drawLine(Point start, Point end) {
		Platform.runLater(() -> drawer.drawLine(start, end));
	}

	private void drawCircle(DrawEvent drawEvent, Point center, double radius) {
		Platform.runLater(() -> drawer.drawCircle(drawEvent, center, radius * Recorder.resMultiplier));
	}
}
