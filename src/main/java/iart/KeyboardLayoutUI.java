package iart;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class KeyboardLayoutUI {
	private Stage stage;
	private Scene scene;
	private Pane pane;

	private KeyboardLayout layout;

	private Text firstKey, secondKey, currKey;
	private final Font font = new Font(14);

	KeyboardLayoutUI(Stage primaryStage) {
		stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setOnCloseRequest((event) -> {
			stage.close();
			primaryStage.close();
			System.exit(0);
		});
		pane = new Pane();
		scene = new Scene(pane, 600, 400);

		stage.setScene(scene);
		stage.setTitle("Layout setup");
		stage.show();

		// TODO: INFO NEEDS UPDATING
		Text info = new Text(0, scene.getY() / 2,
							 "Since I have no way of knowing your keyboard layout, I need to ask a favor of you. " +
							 "I need you to start pressing keys from the top left of your keyboard, to the bottom " +
							 "right. You choose where to start and finish, just note that if you press keys such as " +
							 "volume or power keys, the OS will catch them too.\n" +
							 "Start pressing keys! When you are done one row, press backspace twice to continue to " +
							 "the next one. When you have finished entering your layout, press enter twice."
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

		pane.getChildren().addAll(firstKey, secondKey, currKey);

		layout = new KeyboardLayout(this);
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

	void closeSetupWindow() {
		stage.close();
	}
}
