package mouseart;

import javafx.application.Application;
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

/**
 * Set of states the ImageRecorder can be in. They are used to control the flow of the program.
 * RECORDING indicates the program is recording, which makes ImageRecorder to submit Operation objects to ImageDrawer,
 * to be drawn on to the canvas and the screen.
 * PAUSED indicates the program is not tracking the cursor, until resumed.
 * STOPPED indicates the program has stopped tracking the cursor and is finalizing the image, to save it and start anew.
 */
enum State {
	RECORDING, PAUSED, STOPPED
}

/**
 * Entry point for application.
 * Sets up the image recorder, which continuously tracks the mouse pointer and submits jobs for the drawer to carry out
 * on the canvas.
 */
public class MouseArt extends Application {
	private ImageDrawer im;
	private ImageRecorder ir;

	private Pane pane;
	private MenuItem startRecording, pauseRecording, stopRecording;

	static int screenWidth, screenHeight;
	static int SCENE_WIDTH, SCENE_HEIGHT;

	static State state = State.STOPPED; // Initially, not recording

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX());
		screenHeight = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY());

		SCENE_WIDTH = (int) (screenWidth * 0.25f);
		SCENE_HEIGHT = (int) (screenHeight * 0.25f);

		Scene scene = new Scene(pane = new Pane(), SCENE_WIDTH, SCENE_HEIGHT);

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

		pane.getChildren().addAll(menuBar);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void startRecording() {
		state = State.RECORDING;
		ImageView imageView = new ImageView();

		(im = new ImageDrawer(screenWidth, screenHeight, imageView)).start();
		(ir = new ImageRecorder(im)).start();

		pane.getChildren().add(0, imageView);
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
		if (state == State.STOPPED)
			return;
		state = State.STOPPED;

		try {
			ir.join();
			im.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		saveImage(stage);

		pane = new Pane();
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

		// Show system file chooser (choose file name and save destination)
		File file = fileChooser.showSaveDialog(stage);

		if (file != null) {
			try {
				ImageIO.write(im.getBufferedImage(), "PNG", file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
