package mouseart;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyboardLayout implements NativeKeyListener, Serializable {
	private HashMap<Integer, Point> layout;
	private ArrayList<Integer> rowWidths;
	private int layoutHeight;

	private int currKey, firstKey = -1, secondKey = -1;
	private int currX = 0, currY = 0;

	private boolean ready = false;

	KeyboardLayout() {
		layout = new HashMap<>();

		// TODO: UPDATE
		System.out.println("Since I have no way of knowing your keyboard layout, I need to ask a favor of you.\n" +
						   "I need you to start pressing keys from the top left of your keyboard, to the\n" +
						   "bottom right. You choose where to start and finish, just note that if you\n" +
						   "press keys such as volume or power keys, the OS will catch them too.");
		System.out.println("Start pressing keys! When you are done one row, press backspace twice\n" +
						   "to continue to the next one. When you have finished entering your layout, \n" +
						   "press enter twice.");

		GlobalScreen.addNativeKeyListener(this);

		while (!ready) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(MouseArt.keysFileLoc);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static KeyboardLayout loadKeyboardLayout(String layoutLoc) {
		try {
			FileInputStream fis = new FileInputStream(layoutLoc);
			ObjectInputStream ois = new ObjectInputStream(fis);
			return (KeyboardLayout) ois.readObject();
		} catch (IOException | ClassNotFoundException ignored) {
		}

		System.out.println("Keyboard layout not found at: \"" + layoutLoc + "\"");
		return null;
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
		int prevKey = currKey;
		currKey = nativeKeyEvent.getKeyCode();
		if (firstKey == -1)
			firstKey = currKey;
		else if (secondKey == -1)
			secondKey = currKey;

		if (currKey != prevKey && !layout.containsKey(currKey)) {
			System.out.println("Last input: " + NativeKeyEvent.getKeyText(currKey));
			layout.put(currKey, new Point(currX++, currY));
		} else if (currKey == prevKey && currKey == firstKey) {
			System.out.println("New row");
			rowWidths.add(currX);
			currX = 0;
			currY++;
		} else if (currKey == prevKey && currKey == secondKey) {
			System.out.println("\nKeyboard layout input finished");
			layoutHeight = currY;
			GlobalScreen.removeNativeKeyListener(this);
			ready = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

	}
}
