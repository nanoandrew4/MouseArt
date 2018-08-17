package iart;

import javafx.application.Platform;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controls the backend portion of the keyboard layout setup. Since the layout can not be determined automagically, the
 * user has to enter it once, so it can be saved a reused.
 */
public class KeyboardLayout extends Thread implements NativeKeyListener, Serializable {
	private transient KeyboardLayoutUI layoutUI;

	private HashMap<Integer, Point> layout;
	private ArrayList<Integer> rowWidths;
	private int numOfRows;

	private int currKey, firstKey = -1, secondKey = -1;
	private int currX = 0, currY = 0;

	private boolean ready = false;

	/**
	 * Initializes the class, but to carry out the setup, the thread must be started.
	 *
	 * @param layoutUI UI layout instance, so that info can be relayed back to the UI for a better UX
	 */
	KeyboardLayout(KeyboardLayoutUI layoutUI) {
		this.layoutUI = layoutUI;

		layout = new HashMap<>();
		rowWidths = new ArrayList<>();
	}

	@Override
	public void run() {
		GlobalScreen.addNativeKeyListener(this);

		while (!ready) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ignored) {
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(iArt.keysFileLoc);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
		} catch (IOException e) {
			e.printStackTrace();
		}

		GlobalScreen.removeNativeKeyListener(this);
		Platform.runLater(() -> layoutUI.closeSetupWindow());
	}

	/**
	 * Returns the hashmap containing the mappings of keycodes to the positions of the keys with respect to each other.
	 *
	 * @return Keyboard layout hashmap
	 */
	HashMap<Integer, Point> getLayout() {
		return layout;
	}

	/**
	 * Returns the ArrayList containing the widths of each of the rows that form the keyboard layout.
	 *
	 * @return ArrayList containing the widths of the rows in the layout
	 */
	ArrayList<Integer> getRowWidths() {
		return rowWidths;
	}

	/**
	 * Returns the number of rows in the layout.
	 *
	 * @return Integer representation of the number of rows in the layout
	 */
	int getNumOfRows() {
		return numOfRows;
	}

	/**
	 * Loads a KeyboardLayout from a file. This file must have been previously written by this class for proper
	 * loading.
	 *
	 * @param layoutLoc Path to the file containing the serialized KeyboardLayout
	 * @return KeyboardLayout instance with the previously entered layout
	 */
	static KeyboardLayout loadKeyboardLayout(String layoutLoc) {
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
		if (firstKey == -1) {
			firstKey = currKey;
			layoutUI.updateFirstKeyText(NativeKeyEvent.getKeyText(firstKey));
		} else if (secondKey == -1) {
			secondKey = currKey;
			layoutUI.updateSecondKeyText(NativeKeyEvent.getKeyText(secondKey));
		}

		if (currKey != prevKey && !layout.containsKey(currKey)) {
			layoutUI.updateCurrKeyText(NativeKeyEvent.getKeyText(currKey));
			layout.put(currKey, new Point(currX++, currY));
			layoutUI.updateCurrRowColumn(currX, currY);
		} else if (currKey == prevKey && currKey == firstKey) {
			rowWidths.add(currX);
			currX = 0;
			currY++;
			layoutUI.updateCurrRowColumn(currX, currY);
		} else if (currKey == prevKey && currKey == secondKey) {
			numOfRows = currY + 1;
			rowWidths.add(currX);
			ready = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
	}
}
