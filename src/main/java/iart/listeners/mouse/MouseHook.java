package iart.listeners.mouse;

import iart.Main;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.recorder.Recorder;
import iart.recorder.State;
import iart.transformers.ScreenCoordinatesTransformer;
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
	private Point prevLocation;
	private long lastMove;
	private boolean mousePressed;

	private final int mPressCircleRad;

	private int prevScreenCollectionHash;

	private int xOffset, yOffset;

	private boolean isTransformationNeeded;

	/**
	 * Sets up the mouse listener and registers it as a global listener. Once this constructor returns, the mouse
	 * listener is fully operational, and will start processing mouse movement/click events immediately.
	 *
	 * @param drawer       Drawer instance to draw the lines and mouse clicks with
	 * @param screenWidth  Width of the screen(s) in pixels
	 * @param screenHeight Height of the screen(s) in pixels
	 */
	public MouseHook(Drawer drawer, int screenWidth, int screenHeight) {
		this.drawer = drawer;
		mPressCircleRad = (Math.max(screenWidth, screenHeight)) / 50;

		prevLocation = MouseInfo.getPointerInfo().getLocation();
		lastMove = System.currentTimeMillis();

		GlobalScreen.addNativeMouseListener(this);
		GlobalScreen.addNativeMouseMotionListener(this);

		calibrateMouseCapture();

		prevScreenCollectionHash = Screen.getScreens().hashCode();

		isTransformationNeeded = Screen.getScreens().stream()
				.anyMatch(screen -> !screen.getBounds().contains(0, 0) &&
									!screen.getBounds().contains(0, Main.screenHeight) &&
									!screen.getBounds().contains(Main.screenWidth, 0) &&
									!screen.getBounds().contains(Main.screenWidth, Main.screenHeight));
	}

	private void calibrateMouseCapture() {
		State prevState = Recorder.state;
		Recorder.state = State.CALIBRATING;
		ScreenCoordinatesTransformer.invalidateCurrentInstance();
		prevScreenCollectionHash = Screen.getScreens().hashCode();

		calibrateAxisOffsets();

		Recorder.state = prevState;
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
		drawCircle(DrawEvent.LMOUSE_PRESS, prevLocation.x, prevLocation.y, rand.nextInt(mPressCircleRad) + 5);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {
		mousePressed = false;
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {
		Point location = nativeMouseEvent.getPoint();
		long diff;

		calculateOffsets(location);
		if (Recorder.state != State.CALIBRATING && prevScreenCollectionHash != Screen.getScreens().hashCode())
			calibrateMouseCapture();
		ScreenCoordinatesTransformer.getInstance().transformPoint(location);

		/*
		 * If the mouse has moved, draw a line between previous position and current position.
		 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
		 * cube root of the time elapsed until the mouse was moved again.
		 */
		if (Recorder.state == State.RECORDING) {
			if (!prevLocation.equals(location)) {
				if ((diff = System.currentTimeMillis() - lastMove) > 3000) {
					double radius = getMouseMoveRadius(diff / 1000d);
					drawCircle(DrawEvent.MOVE_OUTER_CIRCLE, location.x, location.y, radius);
					drawCircle(DrawEvent.MOVE_INNER_CIRCLE, location.x, location.y, radius / 10);
				}
				lastMove = System.currentTimeMillis();
				drawLine(prevLocation.x, prevLocation.y, location.x, location.y);
			}
		}
		prevLocation = location;
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
			   (1d + 35d * Math.exp(-0.001d * diffSecs))
			   - 15d;
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {
	}

	private void drawLine(double startX, double startY, double endX, double endY) {
		Platform.runLater(() -> drawer.drawLine(
				startX * Recorder.resMultiplier, startY * Recorder.resMultiplier,
				endX * Recorder.resMultiplier, endY * Recorder.resMultiplier
											   ));
	}

	private void drawCircle(DrawEvent drawEvent, double centreX, double centreY, double radius) {
		Platform.runLater(() -> drawer.drawCircle(
				drawEvent, centreX * Recorder.resMultiplier, centreY * Recorder.resMultiplier,
				radius * Recorder.resMultiplier)
						 );
	}
}
