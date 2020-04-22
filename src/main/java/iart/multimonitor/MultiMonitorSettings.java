package iart.multimonitor;

import iart.GlobalVariables;
import iart.multimonitor.transformers.ScreenCoordinateTransformer;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MultiMonitorSettings {

	private static Scene scene;

	public static void showSettingsWindow(Stage parentStage) {
		Stage stage = new Stage();
		stage.setAlwaysOnTop(true);
		stage.initOwner(parentStage);
		stage.initModality(Modality.APPLICATION_MODAL);
		Pane mainPane = new Pane();

		stage.setTitle("Multi-Monitor settings");

		stage.setScene(scene = new Scene(mainPane, Math.min(1200, GlobalVariables.screenWidth), Math.min(800, GlobalVariables.screenHeight)));
		mainPane.getChildren().addAll(createScreenPreviews(new Scale((scene.getWidth() * 0.6) / GlobalVariables.screenWidth,
																	 (scene.getHeight() * 0.5) / GlobalVariables.screenHeight)));
		stage.show();
	}

	private static List<Node> createScreenPreviews(Scale boundsScaling) {
		return ScreenCoordinateTransformer.getInstance().getScreens().stream().map(screen -> {
			Rectangle realScreenBounds = new Rectangle(screen.getRealBounds().getMinX(), screen.getRealBounds().getMinY(),
													   screen.getRealBounds().getWidth(), screen.getRealBounds().getHeight());
			Rectangle transformedBounds = new Rectangle(screen.getTransformedBounds().getMinX(), screen.getTransformedBounds().getMinY(),
														screen.getTransformedBounds().getWidth(), screen.getTransformedBounds().getHeight());
			realScreenBounds.setStroke(Paint.valueOf("red"));
			realScreenBounds.setFill(Paint.valueOf("transparent"));
			scaleBoundsAndApplyOffsets(realScreenBounds, boundsScaling);
			Label realScreenBoundsLbl = new Label(screen.getRealBounds().getWidth() + "x" + screen.getRealBounds().getHeight() + " @ " +
												  screen.getRealBounds().getMinX() + "," + screen.getRealBounds().getMinY());
			realScreenBoundsLbl.setLayoutX(realScreenBounds.getX() + 5);
			realScreenBoundsLbl.setLayoutY(realScreenBounds.getY() + 5);
			realScreenBoundsLbl.setVisible(false);
			realScreenBoundsLbl.setMouseTransparent(true);
			realScreenBounds.setOnMouseEntered(mouseEvent -> realScreenBoundsLbl.setVisible(true));
			realScreenBounds.setOnMouseExited(mouseEvent -> realScreenBoundsLbl.setVisible(false));

			transformedBounds.setStroke(Paint.valueOf("blue"));
			transformedBounds.setFill(Paint.valueOf("transparent"));
			scaleBoundsAndApplyOffsets(transformedBounds, boundsScaling);
			Label transformedBoundsLbl = new Label(screen.getTransformedBounds().getWidth() + "x" + screen.getTransformedBounds().getHeight() + " @ " +
												   screen.getTransformedBounds().getMinX() + ", " + screen.getTransformedBounds().getMinY());
			transformedBoundsLbl.setLayoutX(transformedBounds.getX() + 5);
			transformedBoundsLbl.setLayoutY(transformedBounds.getY() + 5);
			transformedBoundsLbl.setVisible(false);
			transformedBoundsLbl.setMouseTransparent(true);
			transformedBounds.setOnMouseEntered(mouseEvent -> transformedBoundsLbl.setVisible(true));
			transformedBounds.setOnMouseExited(mouseEvent -> transformedBoundsLbl.setVisible(false));

			return List.of(transformedBounds, realScreenBounds, transformedBoundsLbl, realScreenBoundsLbl);
		}).flatMap(Collection::stream).collect(Collectors.toList());
	}

	private static void scaleBoundsAndApplyOffsets(Rectangle bounds, Scale boundsScaling) {
		bounds.setX(bounds.getX() * boundsScaling.getX() + (scene.getWidth() * 0.2));
		bounds.setY(bounds.getY() * boundsScaling.getY() + (scene.getHeight() * 0.05));
		bounds.setWidth(bounds.getWidth() * boundsScaling.getX());
		bounds.setHeight(bounds.getHeight() * boundsScaling.getY());
	}
}
