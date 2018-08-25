package iart;

import iart.color_schemes.ColorScheme;
import iart.listeners.keyboard.KeyboardLayoutUI;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for application. Initializes the UI portion of the program. Also initializes the keyboard and mouse
 * hooks when recording starts, and if required, initializes the keyboard layout setup process.
 */
public class Main extends Application {
	private Recorder recorder = new Recorder();

	public static int screenWidth, screenHeight;
	private int sceneWidth, sceneHeight;
	private boolean windowFocused = false;

	private Scene previewScene;
	private Group previewGroup;

	private MenuBar menuBar = new MenuBar();
	private MenuItem startRecording = new MenuItem("Start"), pauseRecording = new MenuItem("Pause"),
			stopRecording = new MenuItem("Stop");
	private ImageView geomPreview = new ImageView(); // Canvas preview

	private SnapshotParameters snapshotParameters = new SnapshotParameters();

	// Location on disk of the keyboard layout
	public static final String keysFileLoc = System.getProperty("user.home") + "/.iart_keys";

	private static Spinner<Double> resMultiplierSpinner = new Spinner<>(1d, Double.MAX_VALUE, 1d, 0.1);

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

		sceneWidth = (int) (screenWidth * .25d);
		sceneHeight = (int) (screenHeight * .25d);

		previewScene = new Scene(previewGroup = new Group(), sceneWidth, sceneHeight);

		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		menuBar.setOnMouseEntered(event -> {
			if (Recorder.state == State.RECORDING)
				menuBar.setOpacity(1);
		});
		menuBar.setOnMouseExited(event -> {
			if (Recorder.state == State.RECORDING)
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

		MenuItem resetKeyboardLayout = new MenuItem("Reset keyboard layout");
		resetKeyboardLayout.setOnAction(event -> new KeyboardLayoutUI(primaryStage));
		startRecording.setOnAction(event -> {
			if (recorder.startRecording(this, resMultiplierSpinner.getValue())) {
				menuBar.setOpacity(0.5);
				previewScene.setRoot(previewGroup = new Group(geomPreview, menuBar));
				updateSnapshotParams();
				refreshPreview();
			}
		});
		pauseRecording.setOnAction(event -> recorder.pauseRecording(pauseRecording));
		stopRecording.setOnAction(event -> {
			if (recorder.stopRecording(primaryStage))
				menuBar.setOpacity(1);
		});

		fileMenu.getItems().addAll(resetKeyboardLayout, new SeparatorMenuItem(), startRecording, pauseRecording,
								   stopRecording);

		// Setup color scheme menu
		Menu colorSchemeMenu = new Menu("Color Scheme");
		ToggleGroup tGroup = new ToggleGroup();

		loadColorSchemes();

		Set<String> colorSchemes = ColorScheme.colorSchemes.keySet();
		for (String colorSchemeStr : colorSchemes) {
			RadioMenuItem scheme = new RadioMenuItem(colorSchemeStr);
			scheme.setToggleGroup(tGroup);
			scheme.setOnAction(event -> Recorder.colorScheme = swapColorScheme(colorSchemeStr));
			if (Recorder.colorScheme.getClass() == ColorScheme.colorSchemes.get(colorSchemeStr).getClass())
				scheme.setSelected(true);
			colorSchemeMenu.getItems().add(scheme);
		}

		resMultiplierSpinner.setEditable(true);

		Menu resSpinnerMenu = new Menu("Resolution Multiplier", null, new CustomMenuItem(resMultiplierSpinner, false));

		// Setup menu bar
		menuBar.getMenus().addAll(fileMenu, colorSchemeMenu, resSpinnerMenu);
	}

	/**
	 * Loads the color schemes specified in the colorSchemesStr array. The strings must match actual class names,
	 * otherwise the color scheme will not be loaded.
	 */
	private void loadColorSchemes() {
		for (String s : ColorScheme.colorSchemesStr) {
			try {
				ColorScheme.colorSchemes.put(s, (ColorScheme) Class.forName("iart.color_schemes." + s + "Scheme")
																   .getConstructor().newInstance());
			} catch (Exception e) {
				System.err.println("Color scheme: \"" + s + "\" could not be found.");
			}
		}
	}

	/**
	 * Allows the active color scheme to do some cleanup if necessary before being swapped.
	 *
	 * @param colorSchemeStr Name of the color scheme that is to replace the active one
	 * @return ColorScheme corresponding to the name passed as an argument
	 */
	private ColorScheme swapColorScheme(String colorSchemeStr) {
		Recorder.colorScheme.unregisterColorScheme();
		return ColorScheme.colorSchemes.get(colorSchemeStr);
	}

	/**
	 * Refreshes the preview window in the main stage. Called when the window is active and a shape is drawn through
	 * the Drawer class.
	 */
	public void refreshPreview() {
		if (!windowFocused || recorder.getCanvas() == null)
			return;
		geomPreview.setImage(recorder.getCanvas().snapshot(snapshotParameters, null));
	}

	/**
	 * Sets window resize listeners, and closing listeners.
	 *
	 * @param stage Stage to set listeners for (primaryStage)
	 */
	private void setStageListeners(Stage stage) {
		// Cleanup if window is closed, ensure all threads end
		stage.setOnCloseRequest(event -> {
			Recorder.state = State.STOPPED;
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
	 * Updates the snapshot parameters in order to accurately render the preview of the art being drawn. The snapshot
	 * parameters take care of scaling the original image down to a size that is adequate for the preview window.
	 */
	private void updateSnapshotParams() {
		snapshotParameters.setTransform(
				Transform.scale(sceneWidth / (double) screenWidth, sceneHeight / (double) screenHeight)
		);
	}
}
