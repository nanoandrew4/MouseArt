package iart.listeners.keyboard;

import iart.Drawer;
import iart.Main;
import iart.State;
import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.Random;

/**
 * Listens for keyboard events and triggers draw events to create a visual representation of the users keyboard use.
 */
public class KeyHook implements NativeKeyListener {
	private Drawer drawer;

	private KeyboardLayout layout;
	private Random rand = new Random();

	private int screenWidth, screenHeight;

	private int squareMaxWidth;

	/**
	 * Sets up the keyboard listener and registers it as a global listener. Once this constructor returns, the keyboard
	 * listener is fully operational, and will start processing keystrokes immediately.
	 *
	 * @param drawer
	 * @param screenWidth  Width of the screen(s) in pixels
	 * @param screenHeight Height of the screen(s) in pixels
	 */
	public KeyHook(Drawer drawer, int screenWidth, int screenHeight) {
		this.drawer = drawer;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		squareMaxWidth = (screenWidth > screenHeight ? screenWidth : screenHeight) / 100;

		layout = KeyboardLayout.loadKeyboardLayout(Main.keysFileLoc);
		GlobalScreen.addNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		if (Main.state == State.RECORDING) {
			Point keyPos = layout.getLayout().get(nativeKeyEvent.getKeyCode());

			if (keyPos != null) {
				int keysInRow = layout.getRowWidths().get(keyPos.y);
				int topLeftX = (screenWidth / keysInRow) * keyPos.x;
				int topLeftY = (screenHeight / layout.getNumOfRows()) * keyPos.y;
				int drawPosX = topLeftX + rand.nextInt((screenWidth / keysInRow) - squareMaxWidth);
				int drawPosY = topLeftY + rand.nextInt((screenHeight / layout.getNumOfRows()) - squareMaxWidth);

				Platform.runLater(() -> drawer.drawSquare(drawPosX, drawPosY, rand.nextInt(squareMaxWidth - 10) + 10));
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
		// N/A
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
		// N/A
	}
}
