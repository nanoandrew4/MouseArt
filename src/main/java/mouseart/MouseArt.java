package mouseart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.shape.ArcType;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import mouseart.color_scheme.ColorScheme;
import mouseart.color_scheme.GrayScale;
import mouseart.color_scheme.RGBScale;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for application. Takes care of all the rendering, and anything related to the UI.
 */
public class MouseArt extends Application {
	private MouseHook mouseHook;

	public static Random rand = new Random();

	private int screenWidth, screenHeight;
	private int sceneWidth, sceneHeight;
	private boolean stageMinimized = false;

	static State state = State.STOPPED;

	private Scene geometryScene, previewScene;
	private Group previewGroup;
	private Canvas canvas;
	private GraphicsContext gc;

	private MenuBar menuBar;
	private MenuItem startRecording, pauseRecording, stopRecording;
	private ImageView geomPreview;

	private SnapshotParameters snapshotParameters;

//	private ColorScheme colorScheme = new GrayScale();
	private ColorScheme colorScheme = new RGBScale();

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

		geometryScene = new Scene(new Group(), screenWidth, screenHeight);
		previewScene = new Scene(previewGroup = new Group(), sceneWidth, sceneHeight);

		geomPreview = new ImageView(); // Resized view of the canvas will go in here as an Image
		snapshotParameters = new SnapshotParameters(); // Update scene size with to determine resize of snapshot

		menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		menuBar.setOnMouseEntered(event -> {
			if (state == State.RECORDING)
				menuBar.setOpacity(1);
		});
		menuBar.setOnMouseExited(event -> {
			if (state == State.RECORDING)
				menuBar.setOpacity(0.3);
		});

		Menu fileMenu = new Menu("File");

		startRecording = new MenuItem("Start");
		startRecording.setOnAction(event -> startRecording());

		pauseRecording = new MenuItem("Pause");
		pauseRecording.setOnAction(event -> pauseRecording());

		stopRecording = new MenuItem("Stop");
		stopRecording.setOnAction(event -> stopRecording(primaryStage));

		fileMenu.getItems().addAll(startRecording, pauseRecording, stopRecording);
		menuBar.getMenus().addAll(fileMenu);
		previewGroup.getChildren().addAll(menuBar);

		setStageListeners(primaryStage);
		primaryStage.setScene(previewScene);
		primaryStage.show(); // Invokes scene width and height property listeners
	}

	private void refreshPreview() {
		if (stageMinimized || canvas == null) return;
		geomPreview.setImage(canvas.snapshot(snapshotParameters, null));
	}

	/**
	 * Draws a line on the canvas.
	 *
	 * @param drawEvent Determinant of color through use of color palette, depending on the figure being drawn
	 * @param startX    Line start coordinate on the x axis
	 * @param startY    Line start coordinate on the y axis
	 * @param endX      Line end coordinate on the x axis
	 * @param endY      Line end coordinate on the y axis
	 */
	protected void drawLine(DrawEvent drawEvent, int startX, int startY, int endX, int endY) {
		gc.setStroke(colorScheme.getColor(drawEvent));
		gc.setLineWidth(1);
		gc.strokeLine(startX, startY, endX, endY);
		refreshPreview();
	}

	/**
	 * Draws a circle on the canvas.
	 *
	 * @param drawEvent Determinant of color through use of color palette, depending on the figure being drawn
	 * @param centreX   Circle center on the x axis
	 * @param centreY   Circle center on the y axis
	 * @param radius    Radius of the circle
	 */
	protected void drawCircle(DrawEvent drawEvent, int centreX, int centreY, int radius) {
		gc.setFill(colorScheme.getColor(drawEvent));
		gc.fillArc(centreX - radius / 2, centreY - radius / 2, radius, radius, 0, 360, ArcType.ROUND);
		if (drawEvent == DrawEvent.MOVE_OUTER_CIRCLE)
			gc.strokeArc(centreX - radius / 2, centreY - radius / 2, radius, radius, 0, 360, ArcType.OPEN);
		refreshPreview();
	}

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

		// Track if program is minimized, no need to update UI unnecessarily
		stage.iconifiedProperty().addListener((ov, t, t1) -> stageMinimized = t1);

		// Track if UI is resized, and update previewScene size appropriately
		previewScene.widthProperty().addListener((obs, oldVal, newVal) -> {
			sceneWidth = newVal.intValue();
			snapshotParameters.setTransform(
					Transform.scale(sceneWidth / (double) screenWidth, sceneHeight / (double) screenHeight)
			);
			refreshPreview();
		});

		previewScene.heightProperty().addListener((obs, oldVal, newVal) -> {
			sceneHeight = newVal.intValue();
			snapshotParameters.setTransform(
					Transform.scale(sceneWidth / (double) screenWidth, sceneHeight / (double) screenHeight)
			);
			refreshPreview();
		});
	}

	private void startRecording() {
		if (state != State.STOPPED)
			return;
		state = State.RECORDING;

		menuBar.setOpacity(0.5);

		previewScene.setRoot(previewGroup = new Group(geomPreview, menuBar));
		canvas = new Canvas(screenWidth, screenHeight);
		gc = canvas.getGraphicsContext2D();
		geometryScene.setRoot(new Group(canvas));

		mouseHook = new MouseHook(this);
		mouseHook.prepForRecording();
		GlobalScreen.addNativeMouseListener(mouseHook);
		GlobalScreen.addNativeMouseMotionListener(mouseHook);
	}

	private void pauseRecording() {
		if (state == State.RECORDING) {
			state = State.PAUSED;
			pauseRecording.setText("Resume");
		} else if (state == State.PAUSED) {
			state = State.RECORDING;
			pauseRecording.setText("Pause");
		}
	}

	private void stopRecording(Stage stage) {
		state = State.STOPPED;
		GlobalScreen.removeNativeMouseMotionListener(mouseHook);
		GlobalScreen.removeNativeMouseListener(mouseHook);
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
				ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(new SnapshotParameters(), null), null), "png", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
