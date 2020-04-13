package iart.listeners.keyboard;

import iart.JFXMain;
import iart.draw.Drawer;
import iart.recorder.Recorder;
import iart.recorder.State;
import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.Random;

/**
 * Listens for keyboard events and triggers draw events to create a visual representation of the users keyboard use.
 */
public class KeyboardHook implements NativeKeyListener {
	private final Drawer drawer;

	private final KeyboardLayout layout;
	private final Random rand = new Random();

	private final double screenWidth, screenHeight;

	private final int squareMaxWidth; // Max size that a square drawn by a keystroke can be

	/**
	 * Sets up the keyboard listener and registers it as a global listener. Once this constructor returns, the keyboard
	 * listener is fully operational, and will start processing keystrokes immediately.
	 *
	 * @param drawer       Drawer instance to draw with
	 * @param screenWidth  Width of the screen(s) in pixels
	 * @param screenHeight Height of the screen(s) in pixels
	 */
	public KeyboardHook(Drawer drawer, double screenWidth, double screenHeight) {
		this.drawer = drawer;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		squareMaxWidth = (int) (Math.max(screenWidth, screenHeight) / 100);

		layout = KeyboardLayout.loadKeyboardLayout(JFXMain.keysFileLoc);
		GlobalScreen.addNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		if (Recorder.state == State.RECORDING) {
			Point keyPos = layout.getLayout().get(nativeKeyEvent.getKeyCode());

			if (keyPos != null) {
				int keysInRow = layout.getRowWidths().get(keyPos.y);
				int topLeftX = (int) (screenWidth / keysInRow) * keyPos.x;
				int topLeftY = (int) (screenHeight / layout.getNumOfRows()) * keyPos.y;
				int drawPosX = topLeftX + rand.nextInt((int) (screenWidth / keysInRow) - squareMaxWidth);
				int drawPosY = topLeftY + rand.nextInt((int) (screenHeight / layout.getNumOfRows()) - squareMaxWidth);

				Platform.runLater(() -> drawer.drawSquare(
						new Point(drawPosX, drawPosY), rand.nextInt(squareMaxWidth - 10) + 10)
				);
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
	}
}
