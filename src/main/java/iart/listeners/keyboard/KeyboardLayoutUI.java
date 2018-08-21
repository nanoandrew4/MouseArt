package iart.listeners.keyboard;

import iart.Main;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * UI portion of the keyboard layout setup process.
 */
public class KeyboardLayoutUI {
	private Stage stage;

	private Text firstKey, secondKey, currKey, currRowColumn;
	private final Font font = new Font(14);

	/**
	 * This method launches a new window which displays information relevant to the keyboard layout setup. If the
	 * window is closed before the setup has been finished, the whole program will end.
	 *
	 * @param primaryStage Application stage, which is launched when the program starts, so that it may be closed if
	 *                     the setups does not finish successfully
	 */
	public KeyboardLayoutUI(Stage primaryStage) {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setOnCloseRequest((event) -> {
			stage.close();
			primaryStage.close();
			Platform.exit();
			System.exit(0);
		});
		Pane pane = new Pane();
		Scene scene = new Scene(pane, 600, 400);

		stage.setScene(scene);
		stage.setTitle("Layout setup");
		stage.show();

		Text info = new Text(0, scene.getY() / 2,
							 "Since I have no way of knowing your keyboard layout, I need to ask a favor of you. " +
							 "I need you to start pressing keys from the top left of your keyboard, to the bottom " +
							 "right. You choose where to start and finish, just note that if you press keys such as " +
							 "volume or power keys, the OS will catch them too. The file holding your layout will " +
							 "be stored under: " + Main.keysFileLoc + "\n" +
							 "Start pressing keys! When you are done one row, press the first key you pressed twice." +
							 " " +
							 "When you have finished entering your layout, press the second key you pressed twice."
		);
		info.setFont(font);

		info.wrappingWidthProperty().bind(scene.widthProperty());

		pane.getChildren().add(info);

		firstKey = new Text(0, info.getBoundsInLocal().getHeight() + font.getSize(), "First key: Waiting...");
		firstKey.setFont(font);
		secondKey = new Text(0, info.getBoundsInLocal().getHeight() + font.getSize() * 2, "Second key: Waiting...");
		secondKey.setFont(font);
		currKey = new Text(0, info.getBoundsInLocal().getHeight() + font.getSize() * 3, "Current key: None...");
		currKey.setFont(font);
		currRowColumn = new Text(0, info.getBoundsInLocal().getHeight() + font.getSize() * 4, "Current row/column: " +
																							  "0/0");
		currRowColumn.setFont(font);


		pane.getChildren().addAll(firstKey, secondKey, currKey, currRowColumn);

		KeyboardLayout layout = new KeyboardLayout(this);
		layout.start();
	}

	void updateFirstKeyText(String key) {
		firstKey.setText("First key: " + key);
	}

	void updateSecondKeyText(String key) {
		secondKey.setText("Second key: " + key);
	}

	void updateCurrKeyText(String key) {
		currKey.setText("Current key: " + key);
	}

	void updateCurrRowColumn(int column, int row) {
		currRowColumn.setText("Current column/row: " + column + "/" + row);
	}

	void closeSetupWindow() {
		stage.close();
	}
}
