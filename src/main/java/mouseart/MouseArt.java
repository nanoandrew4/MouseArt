package mouseart;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Entry point for application.
 * Sets up the image recorder, which continuously tracks the mouse pointer and submits jobs for the drawer to carry out
 * on the canvas.
 */
public class MouseArt extends Application {
	private ImageDrawer im;
	private ImageRecorder ir;

	public static Random rand = new Random();

	private Pane pane;
	private MenuItem startRecording, pauseRecording, stopRecording;
	private ImageView imgView;

	static int screenWidth, screenHeight;
	static int sceneWidth, sceneHeight;
	static boolean stageMinimized = false;

	static State state = State.STOPPED; // Initially not recording

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX());
		screenHeight = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY());

		sceneWidth = (int) (screenWidth * 0.25f);
		sceneHeight = (int) (screenHeight * 0.25f);

		Scene scene = new Scene(pane = new Pane(), sceneWidth, sceneHeight);

		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

		Menu fileMenu = new Menu("File");

		startRecording = new MenuItem("Start");
		startRecording.setOnAction(event -> startRecording());

		pauseRecording = new MenuItem("Pause");
		pauseRecording.setOnAction(event -> pauseRecording());

		stopRecording = new MenuItem("Stop");
		stopRecording.setOnAction(event -> stopRecording(primaryStage));

		fileMenu.getItems().addAll(startRecording, pauseRecording, stopRecording);

		menuBar.getMenus().addAll(fileMenu);

		pane.getChildren().addAll(imgView = new ImageView(), menuBar);

		setStageListeners(primaryStage);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void setStageListeners(Stage stage) {
		// Cleanup if window is closed, ensure all threads end
		stage.setOnCloseRequest(event -> {
			state = State.STOPPED;
			try {
				if (ir != null)
					ir.join();
				if (im != null)
					im.join();
			} catch (Exception ignored) {
			}
			Platform.exit();
			System.exit(0);
		});

		// Track if program is minimized, no need to update UI unnecessarily
		stage.iconifiedProperty().addListener((ov, t, t1) -> stageMinimized = t1);

		// Track if UI is resized, and update scene size appropriately
		stage.widthProperty().addListener((obs, oldVal, newVal) -> sceneWidth = newVal.intValue());
		stage.heightProperty().addListener((obs, oldVal, newVal) -> sceneHeight = newVal.intValue());
	}

	private void startRecording() {
		if (state != State.STOPPED)
			return;
		state = State.RECORDING;

		pane.getChildren().set(0, imgView = new ImageView());

		(im = new ImageDrawer(screenWidth, screenHeight, imgView)).start();
		(ir = new ImageRecorder(im)).start();
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

		try {
			ir.join();
			im.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
				ImageIO.write(im.getBufferedImage(), "png", file);
				pane.getChildren().set(0, imgView = new ImageView());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
