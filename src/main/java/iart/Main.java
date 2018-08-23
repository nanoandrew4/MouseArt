package iart;

import iart.color_scheme.ColorScheme;
import iart.color_scheme.GrayscaleScheme;
import iart.draw.DrawEvent;
import iart.draw.Drawer;
import iart.listeners.keyboard.KeyHook;
import iart.listeners.keyboard.KeyboardLayoutUI;
import iart.listeners.mouse.MouseHook;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for application. Initializes the UI portion of the program, and controls all the drawing of shapes and
 * lines on the canvas. Also initializes the keyboard and mouse hooks, and if required, initializes the keyboard
 * layout setup process. TODO: REWRITE
 */
public class Main extends Application {
	private MouseHook mouseHook;
	private KeyHook keyHook;

	public static int screenWidth, screenHeight;
	private int sceneWidth, sceneHeight;
	private boolean windowFocused = false;

	public static State state = State.STOPPED;

	private Scene previewScene;
	private Group previewGroup;
	private Canvas canvas;

	private MenuBar menuBar = new MenuBar();
	private MenuItem startRecording = new MenuItem("Start"), pauseRecording = new MenuItem("Pause"),
			stopRecording = new MenuItem("Stop");
	private ImageView geomPreview = new ImageView(); // Canvas preview

	private SnapshotParameters snapshotParameters = new SnapshotParameters();

	private final String[] colorSchemesStr = {"Grayscale", "ColorWheel", "Rainbow", "GrayscaleWheel"};
	public static ColorScheme colorScheme = new GrayscaleScheme();

	public static String keysFileLoc = System.getProperty("user.home") + "/.iart_keys";

	private static Spinner<Double> resMultiplierSpinner = new Spinner<>(1d, Double.MAX_VALUE, 1d, 0.1);
	public static double resMultiplier = 1d;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		// Get screen sizes, supports multiple monitors
		screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX());
		screenHeight = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY());

		sceneWidth = (int) (screenWidth * .25);
		sceneHeight = (int) (screenHeight * .25);

		previewScene = new Scene(previewGroup = new Group(), sceneWidth, sceneHeight);

		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		menuBar.setOnMouseEntered(event -> {
			if (state == State.RECORDING)
				menuBar.setOpacity(1);
		});
		menuBar.setOnMouseExited(event -> {
			if (state == State.RECORDING)
				menuBar.setOpacity(.2);
		});

		setupMenuBar(primaryStage);
		previewGroup.getChildren().addAll(menuBar);

		setStageListeners(primaryStage);
		primaryStage.setScene(previewScene);
		primaryStage.setTitle("iArt");
		primaryStage.show();

		if (!Files.exists(Paths.get(keysFileLoc)))
			new KeyboardLayoutUI(primaryStage);
	}

	/**
	 * Sets up the menu bar and the menus it includes, which allow the user to start/pause/stop recording and change
	 * color schemes.
	 *
	 * @param primaryStage Main stage, so that if the recording is stopped, a snapshot can be taken and stored
	 */
	private void setupMenuBar(Stage primaryStage) {
		Menu fileMenu = new Menu("File");

		startRecording.setOnAction(event -> startRecording());
		pauseRecording.setOnAction(event -> pauseRecording());
		stopRecording.setOnAction(event -> stopRecording(primaryStage));

		fileMenu.getItems().addAll(startRecording, pauseRecording, stopRecording);

		// Setup color scheme menu
		Menu colorSchemeMenu = new Menu("Color Scheme");
		ToggleGroup tGroup = new ToggleGroup();

		loadColorSchemes();

		Set<String> colorSchemes = ColorScheme.colorSchemes.keySet();
		for (String colorSchemeStr : colorSchemes) {
			RadioMenuItem scheme = new RadioMenuItem(colorSchemeStr);
			scheme.setToggleGroup(tGroup);
			scheme.setOnAction(event -> colorScheme = ColorScheme.colorSchemes.get(colorSchemeStr));
			if (colorScheme.getClass() == ColorScheme.colorSchemes.get(colorSchemeStr).getClass())
				scheme.setSelected(true);
			colorSchemeMenu.getItems().add(scheme);
		}

		resMultiplierSpinner.setEditable(true);

		Menu resSpinnerMenu = new Menu("Resolution Scaling", null, new CustomMenuItem(resMultiplierSpinner, false));

		// Setup menu bar
		menuBar.getMenus().addAll(fileMenu, colorSchemeMenu, resSpinnerMenu);
	}

	/**
	 * Loads the color schemes specified in the colorSchemesStr array. The strings must match actual class names,
	 * otherwise the color scheme will not be loaded.
	 */
	private void loadColorSchemes() {
		for (String s : colorSchemesStr) {
			try {
				ColorScheme.colorSchemes.put(s, (ColorScheme) Class.forName("iart.color_scheme." + s + "Scheme")
																   .getConstructor().newInstance());
			} catch (Exception e) {
				System.err.println("Color scheme: \"" + s + "\" could not be found.");
			}
		}
	}

	/**
	 * Refreshes the preview window in the main stage. Called when the window is active and a shape is drawn through
	 * the Drawer class.
	 */
	public void refreshPreview() {
		if (!windowFocused || canvas == null)
			return;
		geomPreview.setImage(canvas.snapshot(snapshotParameters, null));
	}

	/**
	 * Sets window resize listeners, and closing listeners.
	 *
	 * @param stage Stage to set listeners for (primaryStage)
	 */
	private void setStageListeners(Stage stage) {
		// Cleanup if window is closed, ensure all threads end
		stage.setOnCloseRequest(event -> {
			state = State.STOPPED;
			try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e) {
				e.printStackTrace();
			}
			Platform.exit();
			System.exit(0);
		});

		// Track if window loses focus, no need to waste processing power updating the preview if not focused
		stage.focusedProperty().addListener((observable, oldValue, newValue) -> windowFocused = newValue);

		// Track if UI is resized, and update previewScene size appropriately
		previewScene.widthProperty().addListener((obs, oldVal, newVal) -> {
			sceneWidth = newVal.intValue();
			updateSnapshotParams();
			refreshPreview();
		});

		previewScene.heightProperty().addListener((obs, oldVal, newVal) -> {
			sceneHeight = newVal.intValue();
			updateSnapshotParams();
			refreshPreview();
		});
	}

	/**
	 *
	 */
	private void updateSnapshotParams() {
		snapshotParameters.setTransform(
				Transform.scale(sceneWidth / (double) screenWidth, sceneHeight / (double) screenHeight)
		);
	}

	/**
	 * Starts the mouse and keyboard tracking, and clears the canvas in order to draw on it.
	 */
	private void startRecording() {
		if (state != State.STOPPED)
			return;
		state = State.RECORDING;
		menuBar.setOpacity(0.5);

		resMultiplier = Math.sqrt(resMultiplierSpinner.getValue());

		screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX() *
							 resMultiplier);
		screenHeight = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY() *
							  resMultiplier);
		Scene geometryScene = new Scene(new Group(), screenWidth, screenHeight);

		updateSnapshotParams();

		previewScene.setRoot(previewGroup = new Group(geomPreview, menuBar));
		canvas = new Canvas(screenWidth, screenHeight);

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(colorScheme.getColor(DrawEvent.BACKGROUND, null));
		gc.fillRect(0, 0, screenWidth, screenHeight);
		geometryScene.setRoot(new Group(canvas));
		refreshPreview();

		Drawer drawer = new Drawer(this, gc);

		mouseHook = new MouseHook(drawer, screenWidth, screenHeight);
		keyHook = new KeyHook(drawer, screenWidth, screenHeight);
	}

	/**
	 * Pauses the drawing of the mouse movements and keystrokes. Mouse and keyboard tracking is still active.
	 */
	private void pauseRecording() {
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
	private void stopRecording(Stage stage) {
		state = State.STOPPED;

		GlobalScreen.removeNativeMouseMotionListener(mouseHook);
		GlobalScreen.removeNativeMouseListener(mouseHook);
		GlobalScreen.removeNativeKeyListener(keyHook);

		menuBar.setOpacity(1);
		saveImage(stage);
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
