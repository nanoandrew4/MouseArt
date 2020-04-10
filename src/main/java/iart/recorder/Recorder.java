package iart.recorder;

import iart.Main;
import iart.color_schemes.ColorScheme;
import iart.color_schemes.grayscale_scheme.GrayscaleScheme;
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
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/**
 * This class controls the recording process which results in an image being drawn, through the tracking of
 * keystrokes and mouse actions.
 */
public class Recorder {
	private MouseHook mouseHook;
	private KeyboardHook keyboardHook;

	public static State state = State.STOPPED;
	public static ColorScheme colorScheme = new GrayscaleScheme();
	public static double resMultiplier = 1d;

	private Canvas canvas;

	public Canvas getCanvas() {
		return canvas;
	}

	/**
	 * Starts the mouse and keyboard tracking, and clears the canvas in order to draw on it.
	 */
	public boolean startRecording(final Main main, double resMultiplier) {
		if (state != State.STOPPED)
			return false;

		state = State.PRE_RECORDING;

		Recorder.resMultiplier = resMultiplier;

		Main.resetScreenDimensions();
		Main.screenWidth *= resMultiplier;
		Main.screenHeight *= resMultiplier;

		canvas = new Canvas(Main.screenWidth, Main.screenHeight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(colorScheme.getColor(DrawEvent.BACKGROUND, null));
		gc.fillRect(0, 0, Main.screenWidth, Main.screenHeight);

		Drawer drawer = new Drawer(main, gc);

		mouseHook = new MouseHook(drawer, Main.screenWidth, Main.screenHeight);
		keyboardHook = new KeyboardHook(drawer, Main.screenWidth, Main.screenHeight);

		state = State.RECORDING;

		return true;
	}

	/**
	 * Pauses the drawing of the mouse movements and keystrokes. Mouse and keyboard tracking is still active.
	 */
	public void pauseRecording(MenuItem pauseRecording) {
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
	public boolean stopRecording(final Stage stage) {
		if (state == State.STOPPED)
			return false;
		state = State.STOPPED;

		GlobalScreen.removeNativeMouseMotionListener(mouseHook);
		GlobalScreen.removeNativeMouseListener(mouseHook);
		GlobalScreen.removeNativeKeyListener(keyboardHook);

		promptForFilename(stage);

		return true;
	}

	/**
	 * Prompts user (using system file chooser) for a file name and a destination for the file graphically.
	 * Then saves image as a '.png' in the requested directory, under the requested name.
	 *
	 * @param stage JavaFX stage, required for FileChooser
	 */
	private void promptForFilename(final Stage stage) {
		stage.setTitle("iArt - Saving to disk...");

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PNG Files (*.png)", "*.png")
		);
		fileChooser.setInitialFileName(new Date().toString() + ".png");

		createIArtDirIfNotExists();
		fileChooser.setInitialDirectory(new File(Main.iArtFolderPath));

		// Show system file chooser (choose file name and save destination)
		File file = fileChooser.showSaveDialog(stage);

		saveImage(file);

		stage.setTitle("iArt");
	}

	/**
	 * Creates the default directory for iArt images if it does not exist.
	 */
	public static void createIArtDirIfNotExists() {
		try {
			Path path = Paths.get(Main.iArtFolderPath);
			if (!Files.exists(path))
				Files.createDirectory(path);
		} catch (IOException e) {
			System.err.println("Error creating iArt folder, aborting file save...");
		}
	}

	/**
	 * Saves the current canvas to the file passed as an argument.
	 *
	 * @param file File in which to save the image on the canvas
	 */
	public void saveImage(final File file) {
		if (file != null) {
			WritableImage img = tiledNodeSnapshot(canvas);
			try {
				ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png",
							  file);
			} catch (IOException e) {
				System.err.println("Error writing image to disk");
			}
		}
	}

	/**
	 * Takes a snapshot of a Node instance by tiling the image, taking a snapshot of each tile, and stitching the
	 * tiles together to form the full snapshot of the Node. This is done to prevent a crash which is caused by taking
	 * a snapshot of large images.
	 *
	 * @param node Node to take a snapshot of
	 */
	public static WritableImage tiledNodeSnapshot(final Node node) {
		int width = (int) node.getLayoutBounds().getWidth();
		int height = (int) node.getLayoutBounds().getHeight();
		WritableImage image = new WritableImage(width, height);

		/*
		 * 1,000,000 (1,000 x 1,000) pixels per snapshot is a reasonable number to expect JavaFX to be able to handle,
		 * it is an arbitrary value that is known to work.
		 */
		int horTiles = getNumOfTiles(width, 1000);
		int vertTiles = getNumOfTiles(height, 1000);

		int tileWidth = width / horTiles;
		int tileHeight = height / vertTiles;

		try {
			for (int col = 0; col < vertTiles; col++) {
				for (int row = 0; row < horTiles; row++) {
					int x = row * tileWidth;
					int y = col * tileHeight;
					SnapshotParameters params = new SnapshotParameters();
					params.setViewport(new Rectangle2D(x, y, tileWidth, tileHeight));

					image.getPixelWriter().setPixels(x, y, tileWidth, tileHeight,
													 node.snapshot(params, null).getPixelReader(), 0, 0);
				}
			}
		} catch (Exception e) {
			System.err.println("Error tiling and stitching the image.");
		}

		return image;
	}

	/**
	 * Given a size and a maximum number of pixels to be used on one of the sides of a tile, when breaking down a
	 * larger image, this method returns the number of tiles that are required to evenly split the pixels of the
	 * larger image amongst the tiles, as well as have less pixels on one side of the tile than the specified maximum.
	 *
	 * @param size         Size of one of the sides of the tile (width or height)
	 * @param maxPxPerTile Maximum length one of the sides of the tile can have (max width or height of each tile)
	 * @return Number of tiles to be used that accommodates the requirements
	 */
	private static int getNumOfTiles(int size, int maxPxPerTile) {
		int tiles = 1;
		for (; size / tiles > maxPxPerTile || size % tiles != 0; tiles++) ;
		return tiles;
	}
}
