package iart;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class KeyboardLayout extends Thread implements NativeKeyListener, Serializable{
	private KeyboardLayoutUI layoutUI;

	private HashMap<Integer, Point> layout;
	private ArrayList<Integer> rowWidths;
	private int numOfRows;

	private int currKey, firstKey = -1, secondKey = -1;
	private int currX = 0, currY = 0;

	private boolean ready = false;

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

		layoutUI.closeSetupWindow();
	}

	public HashMap<Integer, Point> getLayout() {
		return layout;
	}

	public ArrayList<Integer> getRowWidths() {
		return rowWidths;
	}

	public int getNumOfRows() {
		return numOfRows;
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
		} else if (currKey == prevKey && currKey == firstKey) {
			System.out.println("New row");
			rowWidths.add(currX);
			currX = 0;
			currY++;
		} else if (currKey == prevKey && currKey == secondKey) {
			System.out.println("\nKeyboard layout input finished");
			numOfRows = currY + 1;
			rowWidths.add(currX);
			GlobalScreen.removeNativeKeyListener(this);
			ready = true;
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

	}
}
