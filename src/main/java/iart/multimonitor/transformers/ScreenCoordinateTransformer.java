package iart.multimonitor.transformers;

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

	private List<Screen> screens;

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

	public List<Screen> getScreens() {
		return screens;
	}

	public void transformPoint(Point p) {
		screens.stream().filter(screen -> screen.getRealBounds().contains(p.x, p.y)).findFirst().ifPresent(screen -> screen.getTransformer().transform(p));
	}

	private List<Pair<Screen, ScreenSides>> findScreenSidesWithoutNeighbours() {
		List<Pair<Screen, ScreenSides>> sidesWithoutDirectNeighbour = new LinkedList<>();
		screens.forEach(screen -> {
			if (screen.getRealBounds().getMaxX() < GlobalVariables.screenWidth) {
				boolean hasNoRightNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyOnRightSide);
				if (hasNoRightNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.RIGHT));
			}
			if (screen.getRealBounds().getMinX() > 0) {
				boolean hasNoLeftNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyOnLeftSide);
				if (hasNoLeftNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.LEFT));
			}
			if (screen.getRealBounds().getMaxY() < GlobalVariables.screenHeight) {
				boolean hasNoBottomNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyOnBottomSide);
				if (hasNoBottomNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.BOTTOM));
			}
			if (screen.getRealBounds().getMinY() > 0) {
				boolean hasNoTopNeighbour = screens.stream().map(Screen::getRealBounds).noneMatch(screen::hasNeighbourDirectlyOnTopSide);
				if (hasNoTopNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.TOP));
			}
		});

		// Arrange so horizontal transformations are applied first, then vertical transformations
		sidesWithoutDirectNeighbour.sort((s1, s2) -> s1.getValue().isHorizontalSide() ? -1 : (s2.getValue().isHorizontalSide() ? 1 : 0));
		return sidesWithoutDirectNeighbour;
	}

	private void calculateTransformedBounds(List<Pair<Screen, ScreenSides>> screenSidesWithoutNeighbours) {
		screenSidesWithoutNeighbours.forEach(screenSide -> {
			if (screenSide.getValue() == ScreenSides.LEFT)
				calculateTransformedBoundsLeftSide(screenSide);
			else if (screenSide.getValue() == ScreenSides.RIGHT)
				calculateTransformedBoundsRightSide(screenSide);
			else if (screenSide.getValue() == ScreenSides.TOP)
				calculateTransformedBoundsTopSide(screenSide);
			else if (screenSide.getValue() == ScreenSides.BOTTOM)
				calculateTransformedBoundsBottomSide(screenSide);
		});
	}

	private void calculateTransformedBoundsRightSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToRight = currentScreen::hasNeighbourOnRightSide;
		Comparator<Rectangle2D> minXComparator = Comparator.comparingDouble(Rectangle2D::getMinX);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getTransformedBounds).filter(hasNeighbourToRight).min(minXComparator).orElse(null);

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

		boolean shareUnreachableAreaHorizontally = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableAreaHorizontally ? (screenBounds.getMaxX() + (nearestScreenBounds.getMinX() - screenBounds.getMaxX()) / 2) : nearestScreenBounds.getMinX();
	}

	private void calculateTransformedBoundsLeftSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToLeft = currentScreen::hasNeighbourOnLeftSide;
		Comparator<Rectangle2D> maxXComparator = Comparator.comparingDouble(Rectangle2D::getMaxX);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getTransformedBounds).filter(hasNeighbourToLeft).max(maxXComparator).orElse(null);

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

		boolean shareUnreachableAreaHorizontally = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableAreaHorizontally ? (screenBounds.getMinX() - (screenBounds.getMinX() - nearestScreenBounds.getMaxX()) / 2) : nearestScreenBounds.getMaxX();
	}

	private void calculateTransformedBoundsTopSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourOnTop = currentScreen::hasNeighbourOnTopSide;
		Comparator<Rectangle2D> maxYComparator = Comparator.comparingDouble(Rectangle2D::getMaxY);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getTransformedBounds).filter(hasNeighbourOnTop).max(maxYComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentScreen.getTransformedBounds();
		currentScreen.setTransformedBoundsMinY(calculateTransformedBoundsMinY(currentScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentScreen.getTransformedBounds();
		boolean newBoundsIntersect = screens.stream().filter(screen -> !screen.equals(currentScreen)).map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining top side transformed bounds for screen: " + currentScreen);
			currentScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private double calculateTransformedBoundsMinY(Screen screen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return 0;

		Rectangle2D screenBounds = screen.getRealBounds();

		double sharedMinY = screenBounds.getMinY() - (screenBounds.getMinY() - nearestScreenBounds.getMaxY()) / 2;
		boolean shareUnreachableAreaVertically = canShareUnreachableVerticalArea(screenBounds, nearestScreenBounds);
		boolean resultingTransformationsIntersect = willResultingMinYTransformationCauseIntersections(screen, nearestScreenBounds, sharedMinY);

		return shareUnreachableAreaVertically && !resultingTransformationsIntersect ? sharedMinY : nearestScreenBounds.getMaxY();
	}

	private boolean willResultingMinYTransformationCauseIntersections(Screen screen, Rectangle2D nearestScreenBounds, double sharedMinY) {
		Rectangle2D newTransformedBounds = new Rectangle2D(screen.getTransformedBounds().getMinX(), sharedMinY, screen.getTransformedBounds().getWidth(),
														   screen.getTransformedBounds().getMaxY() - sharedMinY);
		boolean screenTransformationIntersects = screens.stream().filter(screen1 -> !screen.equals(screen1))
				.map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);

		Rectangle2D nearestScreenNewBounds = new Rectangle2D(nearestScreenBounds.getMinX(), nearestScreenBounds.getMinY(),
															 nearestScreenBounds.getWidth(), sharedMinY - nearestScreenBounds.getMinY());
		boolean nearestScreenTransformationIntersects = screens.stream().map(Screen::getTransformedBounds)
				.filter(bounds -> !bounds.equals(nearestScreenBounds)).anyMatch(nearestScreenNewBounds::intersects);

		return screenTransformationIntersects || nearestScreenTransformationIntersects;
	}

	private void calculateTransformedBoundsBottomSide(Pair<Screen, ScreenSides> screenSide) {
		Screen currentScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourOnBottom = currentScreen::hasNeighbourOnBottomSide;
		Comparator<Rectangle2D> minYComparator = Comparator.comparingDouble(Rectangle2D::getMinY);
		Rectangle2D nearestScreenBounds = screens.stream().map(Screen::getTransformedBounds).filter(hasNeighbourOnBottom).min(minYComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentScreen.getTransformedBounds();
		currentScreen.setTransformedBoundsMaxY(calculateTransformedBoundsMaxY(currentScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentScreen.getTransformedBounds();
		boolean newBoundsIntersect = screens.stream().filter(screen -> !screen.equals(currentScreen)).map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining bottom side transformed bounds for screen: " + currentScreen);
			currentScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private double calculateTransformedBoundsMaxY(Screen screen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return GlobalVariables.screenHeight;

		Rectangle2D screenBounds = screen.getRealBounds();

		double sharedMaxY = screenBounds.getMaxY() + (nearestScreenBounds.getMinY() - screenBounds.getMaxY()) / 2;
		boolean shareUnreachableAreaVertically = canShareUnreachableVerticalArea(screenBounds, nearestScreenBounds);
		boolean resultingTransformationsIntersect = willResultingMaxYTransformationCauseIntersections(screen, nearestScreenBounds, sharedMaxY);

		return shareUnreachableAreaVertically && !resultingTransformationsIntersect ? sharedMaxY : nearestScreenBounds.getMinY();
	}

	private boolean willResultingMaxYTransformationCauseIntersections(Screen screen, Rectangle2D nearestScreenBounds, double sharedMaxY) {
		Rectangle2D newTransformedBounds = new Rectangle2D(screen.getTransformedBounds().getMinX(), screen.getTransformedBounds().getMinY(),
														   screen.getTransformedBounds().getWidth(), sharedMaxY - screen.getTransformedBounds().getMinY());
		boolean screenTransformationIntersects = screens.stream().filter(screen1 -> !screen.equals(screen1))
				.map(Screen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);

		Rectangle2D nearestScreenNewBounds = new Rectangle2D(nearestScreenBounds.getMinX(), sharedMaxY,
															 nearestScreenBounds.getWidth(), nearestScreenBounds.getMaxY() - sharedMaxY);
		boolean nearestScreenTransformationIntersects = screens.stream().map(Screen::getTransformedBounds)
				.filter(bounds -> !bounds.equals(nearestScreenBounds)).anyMatch(nearestScreenNewBounds::intersects);

		return screenTransformationIntersects || nearestScreenTransformationIntersects;
	}

	private boolean canShareUnreachableHorizontalArea(Rectangle2D bounds1, Rectangle2D bounds2) {
		return (bounds1.getMinY() >= bounds2.getMinY() && bounds1.getMaxY() <= bounds2.getMaxY()) ||
			   (bounds1.getMinY() <= bounds2.getMinY() && bounds1.getMaxY() >= bounds2.getMaxY());
	}

	private boolean canShareUnreachableVerticalArea(Rectangle2D bounds1, Rectangle2D bounds2) {
		return (bounds1.getMinX() >= bounds2.getMinX() && bounds1.getMaxX() <= bounds2.getMaxX()) ||
			   (bounds1.getMinX() <= bounds2.getMinX() && bounds1.getMaxX() >= bounds2.getMaxX());
	}
}
