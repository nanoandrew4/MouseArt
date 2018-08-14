package mouseart;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.util.HashMap;

public class KeyHook implements NativeKeyListener {
	private KeyboardLayout layout;

	KeyHook(int screenWidth, int screenHeight) {
		GlobalScreen.addNativeKeyListener(this);
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {

	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

	}
}
