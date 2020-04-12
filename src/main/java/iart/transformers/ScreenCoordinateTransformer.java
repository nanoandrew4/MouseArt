package iart.transformers;

import iart.GlobalVariables;
import javafx.geometry.Rectangle2D;
import javafx.util.Pair;

import java.awt.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ScreenCoordinateTransformer {

	private static ScreenCoordinateTransformer instance;

	private static List<Screen> screens;

	private ScreenCoordinateTransformer() {
		screens = Screen.generateScreens();

		calculateTransformedBounds(findScreenSidesWithoutNeighbours());
	}

	public static void createNewInstance() {
		instance = new ScreenCoordinateTransformer();
	}

	public static ScreenCoordinateTransformer getInstance() {
		if (instance == null)
			instance = new ScreenCoordinateTransformer();
		return instance;
	}

	public void transformPoint(Point p) {
		screens.stream().filter(screen -> screen.getRealBounds().contains(p.x, p.y)).findFirst().ifPresent(screen -> screen.getTransformer().transform(p));
	}

	private List<Pair<Screen, ScreenSides>> findScreenSidesWithoutNeighbours() {
		List<Pair<Screen, ScreenSides>> sidesWithoutDirectNeighbour = new LinkedList<>();
		screens.forEach(screen -> {
			if (screen.getRealBounds().getMaxX() < GlobalVariables.screenWidth) {
				boolean hasNoRightNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyToRight);
				if (hasNoRightNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.RIGHT));
			}
			if (screen.getRealBounds().getMinX() > 0) {
				boolean hasNoLeftNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyToLeft);
				if (hasNoLeftNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.LEFT));
			}
		});

		return sidesWithoutDirectNeighbour;
	}

	private void calculateTransformedBounds(List<Pair<Screen, ScreenSides>> screenSidesWithoutNeighbours) {
		screenSidesWithoutNeighbours.forEach(screenSide -> {
			if (screenSide.getValue() == ScreenSides.LEFT)
				calculateTransformedBoundsLeftSide(screenSide);
			else if (screenSide.getValue() == ScreenSides.RIGHT)
				calculateTransformedBoundsRightSide(screenSide);
		});
	}

	private void calculateTransformedBoundsRightSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToRight = currentScreen::hasNeighbourToRight;
		Comparator<Rectangle2D> minXComparator = Comparator.comparingDouble(Rectangle2D::getMinX);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getRealBounds).filter(hasNeighbourToRight).min(minXComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentScreen.getTransformedBounds();
		currentScreen.setTransformedBoundsMaxX(calculateTransformedBoundsMaxX(currentScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentScreen.getTransformedBounds();
		boolean newBoundsIntersect = screens.stream().filter(screen -> !screen.equals(currentScreen)).map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining right side transformed bounds for screen: " + currentScreen);
			currentScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private double calculateTransformedBoundsMaxX(Screen screen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return GlobalVariables.screenWidth;

		Rectangle2D screenBounds = screen.getRealBounds();

		boolean shareUnreachableArea = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableArea ? (screenBounds.getMaxX() + (nearestScreenBounds.getMinX() - screenBounds.getMaxX()) / 2) : nearestScreenBounds.getMinX();
	}

	private void calculateTransformedBoundsLeftSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToLeft = currentScreen::hasNeighbourToLeft;
		Comparator<Rectangle2D> maxXComparator = Comparator.comparingDouble(Rectangle2D::getMaxX);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getRealBounds).filter(hasNeighbourToLeft).min(maxXComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentScreen.getTransformedBounds();
		currentScreen.setTransformedBoundsMinX(calculateTransformedBoundsMinX(currentScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentScreen.getTransformedBounds();
		boolean newBoundsIntersect = screens.stream().filter(screen -> !screen.equals(currentScreen)).map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining left side transformed bounds for screen: " + currentScreen);
			currentScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private double calculateTransformedBoundsMinX(Screen screen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return 0;

		Rectangle2D screenBounds = screen.getRealBounds();

		boolean shareUnreachableArea = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableArea ? (screenBounds.getMinX() - (screenBounds.getMinX() - nearestScreenBounds.getMaxX()) / 2) : nearestScreenBounds.getMaxX();
	}

	private boolean canShareUnreachableHorizontalArea(Rectangle2D bounds1, Rectangle2D bounds2) {
		return (bounds1.getMinY() >= bounds2.getMinY() && bounds1.getMaxY() <= bounds2.getMaxY()) ||
			   (bounds1.getMinY() <= bounds2.getMinY() && bounds1.getMaxY() >= bounds2.getMaxY());
	}
}
