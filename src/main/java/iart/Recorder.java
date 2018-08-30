package iart;

import iart.color_schemes.ColorScheme;
import iart.color_schemes.GrayscaleScheme;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.listeners.keyboard.KeyboardHook;
import iart.listeners.mouse.MouseHook;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 *
 */
public class Recorder {
	private MouseHook mouseHook;
	private KeyboardHook keyboardHook;

	public static State state = State.STOPPED;
	public static ColorScheme colorScheme = new GrayscaleScheme();
	public static double resMultiplier = 1d;

	private Canvas canvas;

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
		stage.setTitle("iArt - Saving to disk...");

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png")
		);
		fileChooser.setInitialFileName("test.png");

		// Show system file chooser (choose file name and save destination)
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			WritableImage img = exportCanvasToImg(canvas);
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png",
							  file);
			} catch (IOException e) {
				System.err.println("Error writing image to disk");
			}
		}

		stage.setTitle("iArt");
	}

	/**
	 * https://stackoverflow.com/a/51766048
	 * @param node
	 */
	static WritableImage exportCanvasToImg(final Node node) {
		final int w = (int) node.getLayoutBounds().getWidth();
		final int h = (int) node.getLayoutBounds().getHeight();
		final WritableImage full = new WritableImage(w, h);

		// defines the number of tiles to export (use higher value for bigger resolution)
		final int size = Math.max(w / 1920, Math.max(h / 1080, 1));
		final int tileWidth = w / size;
		final int tileHeight = h / size;

		try {
			for (int col = 0; col < size; ++col) {
				for (int row = 0; row < size; ++row) {
					final int x = row * tileWidth;
					final int y = col * tileHeight;
					final SnapshotParameters params = new SnapshotParameters();
					params.setViewport(new Rectangle2D(x, y, tileWidth, tileHeight));

					full.getPixelWriter().setPixels(x, y, tileWidth, tileHeight, node.snapshot(params, null).getPixelReader(), 0, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return full;
	}
}
