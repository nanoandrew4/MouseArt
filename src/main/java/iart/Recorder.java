package iart;

import iart.color_schemes.ColorScheme;
import iart.color_schemes.GrayscaleScheme;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.listeners.keyboard.KeyboardHook;
import iart.listeners.mouse.MouseHook;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Recorder {
	private MouseHook mouseHook;
	private KeyboardHook keyboardHook;

	public static State state = State.STOPPED;
	public static ColorScheme colorScheme = new GrayscaleScheme();
	public static double resMultiplier = 1d;

	private Canvas canvas;

	Recorder() {

	}

	Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Starts the mouse and keyboard tracking, and clears the canvas in order to draw on it.
	 */
	boolean startRecording(Main main, double resMultiplier) {
		if (state != State.STOPPED)
			return false;
		state = State.RECORDING;

		Recorder.resMultiplier = resMultiplier;

		Main.screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX() *
								  resMultiplier);
		Main.screenHeight = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY() *
								   resMultiplier);

		canvas = new Canvas(Main.screenWidth, Main.screenHeight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(colorScheme.getColor(DrawEvent.BACKGROUND, null));
		gc.fillRect(0, 0, Main.screenWidth, Main.screenHeight);

		Drawer drawer = new Drawer(main, gc);

		mouseHook = new MouseHook(drawer, Main.screenWidth, Main.screenHeight);
		keyboardHook = new KeyboardHook(drawer, Main.screenWidth, Main.screenHeight);

		return true;
	}

	/**
	 * Pauses the drawing of the mouse movements and keystrokes. Mouse and keyboard tracking is still active.
	 */
	void pauseRecording(MenuItem pauseRecording) {
		if (state == State.RECORDING) {
			state = State.PAUSED;
			pauseRecording.setText("Resume");
		} else if (state == State.PAUSED) {
			state = State.RECORDING;
			pauseRecording.setText("Pause");
		}
	}

	/**
	 * Stops the drawing process, removes mouse and keyboard trackers, and saves the canvas to an image.
	 *
	 * @param stage Stage which contains the canvas that was being drawn to, so that it can be saved as an image
	 */
	boolean stopRecording(Stage stage) {
		if (state == State.STOPPED)
			return false;
		state = State.STOPPED;

		GlobalScreen.removeNativeMouseMotionListener(mouseHook);
		GlobalScreen.removeNativeMouseListener(mouseHook);
		GlobalScreen.removeNativeKeyListener(keyboardHook);

		saveImage(stage);

		return true;
	}

	/**
	 * Prompts user (using system file chooser) for a file name and a destination for the file graphically.
	 * Then saves image as a '.png' in the requested directory, under the requested name.
	 *
	 * @param stage JavaFX stage, required for FileChooser
	 */
	private void saveImage(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png")
		);
		fileChooser.setInitialFileName("test.png");

		// Show system file chooser (choose file name and save destination)
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(new SnapshotParameters(), null), null), "png",
							  file);
			} catch (IOException e) {
				System.err.println("Error writing image to disk...");
			}
		}
	}
}
