package iart.draw;

import iart.GlobalVariables;
import javafx.stage.Screen;

import java.awt.geom.Point2D;

public class CoordinateCorrector {
	interface Corrector {
		void correct(Point2D point2D);
	}

	private Corrector corrector;
	private int primaryScreen = 0, xOffset, yOffset;

	CoordinateCorrector() {
		for (int i = 1; i < Screen.getScreens().size(); ++i) {
			if (Screen.getScreens().get(0).equals(Screen.getPrimary())) {
				primaryScreen = i;
				break;
			}
		}

		if (GlobalVariables.screenHeight / GlobalVariables.screenWidth > 0.75d) // Vertical layout
			corrector = this::correctVertically;
		else if (GlobalVariables.screenWidth / GlobalVariables.screenHeight > 2d) // Horizontal layout
			corrector = this::correctHorizontally;
		else { // Square layout (vertical and horizontal)
			corrector = (point2D -> {
				correctHorizontally(point2D);
				correctVertically(point2D);
			});
		}
	}

	public void correct(Point2D point2D) {
		corrector.correct(point2D);
	}

	private void correctHorizontally(Point2D point2D) {

	}

	private void correctVertically(Point2D point2D) {

	}
}
