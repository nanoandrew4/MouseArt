package mouseart;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.Random;

public class KeyHook implements NativeKeyListener {
	private MouseArt mouseArt;

	private KeyboardLayout layout;
	private Random rand = new Random();

	private int screenWidth, screenHeight;

	private int squareMaxWidth = 50; // TODO: MAYBE USE NON-MAGIC NUMBER?

	KeyHook(MouseArt mouseArt, int screenWidth, int screenHeight) {
		this.mouseArt = mouseArt;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		layout = KeyboardLayout.loadKeyboardLayout(MouseArt.keysFileLoc);
		GlobalScreen.addNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		Point keyPos = layout.getLayout().get(nativeKeyEvent.getKeyCode());

		if (keyPos != null) {
			int keysInRow = layout.getRowWidths().get(keyPos.y);
			int topLeftX = (screenWidth / keysInRow) * keyPos.x;
			int topLeftY = (screenHeight / layout.getNumOfRows()) * keyPos.y;
			int drawPosX = topLeftX + rand.nextInt((screenWidth / keysInRow) - squareMaxWidth);
			int drawPosY = topLeftY + rand.nextInt((screenHeight / layout.getNumOfRows()) - squareMaxWidth);

			Platform.runLater(() -> mouseArt.drawSquare(drawPosX, drawPosY, rand.nextInt(squareMaxWidth - 10) + 10));
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
