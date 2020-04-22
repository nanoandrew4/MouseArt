package iart.listeners.mouse;

import iart.GlobalVariables;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.multimonitor.transformers.ScreenCoordinateTransformer;
import iart.recorder.Recorder;
import iart.recorder.State;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
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
	private final Drawer drawer;

	private final Random rand = new Random();
	private Point prevLocation, dragStartLocation;
	private long lastMove;
	private boolean mousePressed;

	private final int mPressCircleRad;

	private int prevScreenCollectionHash;

	private int xOffset, yOffset;

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
		mPressCircleRad = (int) (Math.max(screenWidth, screenHeight) / 50);

		prevLocation = MouseInfo.getPointerInfo().getLocation();
		lastMove = System.currentTimeMillis();

		prevScreenCollectionHash = Screen.getScreens().hashCode();

		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);

		calibrateMouseCapture();
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
		return ((Math.min(GlobalVariables.getVirtualScreenWidth(), GlobalVariables.getVirtualScreenHeight())) / 4d) /
			   (1d + 35d * Math.exp(-0.001d * diffSecs)) - 15d;
	}

	/**
	 * Move the mouse cursor to the top leftmost and bottom rightmost corners of each monitor. This way, the
	 * {@link this#nativeMouseMoved(NativeMouseEvent)} method can calibrate itself (calculate the x/y offsets it needs
	 * to apply, since the JNativeHook library miscalculates the x/y coords of the cursor on some multiple monitor setups).
	 */
	private void calibrateAxisOffsets() {
		xOffset = 0;
		yOffset = 0;
		try {
			final Point originalMousePos = MouseInfo.getPointerInfo().getLocation();
			for (Screen s : Screen.getScreens()) {
				final Rectangle2D b = s.getBounds();
				new Robot().mouseMove((int) b.getMinX(), (int) b.getMinY());
				new Robot().mouseMove((int) b.getMaxX(), (int) b.getMaxY());
			}
			new Robot().mouseMove((int) originalMousePos.getX(), (int) originalMousePos.getY());
		} catch (AWTException ignored) {
			System.err.println("Error setting up the virtual canvas");
		}
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

	private void calibrateMouseCapture() {
		State prevState = Recorder.state;
		Recorder.state = State.CALIBRATING;
		ScreenCoordinateTransformer.createNewInstance();
		prevScreenCollectionHash = Screen.getScreens().hashCode();

		calibrateAxisOffsets();

		Recorder.state = prevState;
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		if (dragStartLocation != null) {
			// TODO: Draw rectangle with semi transparent fill
			Point dragEndLocation = nativeMouseEvent.getPoint();
			dragStartLocation = null;
		}
		mousePressed = false;
	}

	/**
	 * Calculate the offsets, so that the location supplied by JNativeHook can be corrected, if necessary.
	 * For example, a 3 monitor setup, with two monitors on top and one below and between them where the bottom
	 * monitor is the primary monitor will cause a miscalculation by JNativeHook.
	 *
	 * @param currLocation Current location of the mouse cursor as reported by JNativeHook
	 */
	private void calculateOffsets(Point currLocation) {
		if (currLocation.getX() + xOffset < 0)
			xOffset = (int) -currLocation.getX();
		if (currLocation.getY() + yOffset < 0)
			yOffset = (int) -currLocation.getY();
		currLocation.setLocation(currLocation.x + xOffset, currLocation.y + yOffset);
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		Point location = nativeMouseEvent.getPoint();
		long diff;

		calculateOffsets(location);
		if (Recorder.state != State.CALIBRATING && prevScreenCollectionHash != Screen.getScreens().hashCode())
			calibrateMouseCapture();
		ScreenCoordinateTransformer.getInstance().transformPoint(location);

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

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
		if (dragStartLocation == null)
			dragStartLocation = nativeMouseEvent.getPoint();
	}

	private void drawLine(Point start, Point end) {
		Platform.runLater(() -> drawer.drawLine(start, end));
	}

	private void drawCircle(DrawEvent drawEvent, Point center, double radius) {
		Platform.runLater(() -> drawer.drawCircle(drawEvent, center, radius));
	}
}
