package iart.multimonitor.transformers;

import iart.GlobalVariables;
import javafx.geometry.Rectangle2D;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ScreenCoordinateTransformerGenerator {
	public static void generate() {
		TransformableScreen.generateScreens();

		calculateTransformedBounds(findScreenSidesWithoutNeighbours());
	}

	private static List<Pair<TransformableScreen, ScreenSides>> findScreenSidesWithoutNeighbours() {
		List<Pair<TransformableScreen, ScreenSides>> sidesWithoutDirectNeighbour = new LinkedList<>();
		TransformableScreen.getTransformableScreens().forEach(screen -> {
			if (screen.getRealBounds().getMaxX() < GlobalVariables.screenWidth) {
				boolean hasNoRightNeighbour = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getRealBounds).noneMatch(screen::isNeighbouringDirectlyOnRightSide);
				if (hasNoRightNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.RIGHT));
			}
			if (screen.getRealBounds().getMinX() > 0) {
				boolean hasNoLeftNeighbour = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getRealBounds).noneMatch(screen::isNeighbouringDirectlyOnLeftSide);
				if (hasNoLeftNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.LEFT));
			}
			if (screen.getRealBounds().getMaxY() < GlobalVariables.screenHeight) {
				boolean hasNoBottomNeighbour = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getRealBounds).noneMatch(screen::isNeighbouringDirectlyOnBottomSide);
				if (hasNoBottomNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.BOTTOM));
			}
			if (screen.getRealBounds().getMinY() > 0) {
				boolean hasNoTopNeighbour = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getRealBounds).noneMatch(screen::isNeighbouringDirectlyOnTopSide);
				if (hasNoTopNeighbour)
					sidesWithoutDirectNeighbour.add(new Pair<>(screen, ScreenSides.TOP));
			}
		});

		// Arrange so horizontal transformations are applied first, then vertical transformations
		sidesWithoutDirectNeighbour.sort((s1, s2) -> s1.getValue().isHorizontalSide() ? -1 : (s2.getValue().isHorizontalSide() ? 1 : 0));
		return sidesWithoutDirectNeighbour;
	}

	private static void calculateTransformedBounds(List<Pair<TransformableScreen, ScreenSides>> screenSidesWithoutNeighbours) {
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

	private static void calculateTransformedBoundsRightSide(Pair<TransformableScreen, ScreenSides> screenSide) {
		TransformableScreen currentTransformableScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToRight = currentTransformableScreen::isNeighbouringOnRightSide;
		Comparator<Rectangle2D> minXComparator = Comparator.comparingDouble(Rectangle2D::getMinX);
		Rectangle2D nearestScreenBounds = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds).filter(hasNeighbourToRight).min(minXComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentTransformableScreen.getTransformedBounds();
		currentTransformableScreen.setTransformedBoundsMaxX(calculateTransformedBoundsMaxX(currentTransformableScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentTransformableScreen.getTransformedBounds();
		boolean newBoundsIntersect = TransformableScreen.getTransformableScreens().stream().filter(screen -> !screen.equals(currentTransformableScreen)).map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining right side transformed bounds for screen: " + currentTransformableScreen);
			currentTransformableScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private static double calculateTransformedBoundsMaxX(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return GlobalVariables.screenWidth;

		Rectangle2D screenBounds = transformableScreen.getRealBounds();

		boolean shareUnreachableAreaHorizontally = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableAreaHorizontally ? (screenBounds.getMaxX() + (nearestScreenBounds.getMinX() - screenBounds.getMaxX()) / 2) : nearestScreenBounds.getMinX();
	}

	private static void calculateTransformedBoundsLeftSide(Pair<TransformableScreen, ScreenSides> screenSide) {
		TransformableScreen currentTransformableScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourToLeft = currentTransformableScreen::isNeighbouringOnLeftSide;
		Comparator<Rectangle2D> maxXComparator = Comparator.comparingDouble(Rectangle2D::getMaxX);
		Rectangle2D nearestScreenBounds = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds).filter(hasNeighbourToLeft).max(maxXComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentTransformableScreen.getTransformedBounds();
		currentTransformableScreen.setTransformedBoundsMinX(calculateTransformedBoundsMinX(currentTransformableScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentTransformableScreen.getTransformedBounds();
		boolean newBoundsIntersect = TransformableScreen.getTransformableScreens().stream().filter(screen -> !screen.equals(currentTransformableScreen)).map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining left side transformed bounds for screen: " + currentTransformableScreen);
			currentTransformableScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private static double calculateTransformedBoundsMinX(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return 0;

		Rectangle2D screenBounds = transformableScreen.getRealBounds();

		boolean shareUnreachableAreaHorizontally = canShareUnreachableHorizontalArea(screenBounds, nearestScreenBounds);
		return shareUnreachableAreaHorizontally ? (screenBounds.getMinX() - (screenBounds.getMinX() - nearestScreenBounds.getMaxX()) / 2) : nearestScreenBounds.getMaxX();
	}

	private static void calculateTransformedBoundsTopSide(Pair<TransformableScreen, ScreenSides> screenSide) {
		TransformableScreen currentTransformableScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourOnTop = currentTransformableScreen::isNeighbouringOnTopSide;
		Comparator<Rectangle2D> maxYComparator = Comparator.comparingDouble(Rectangle2D::getMaxY);
		Rectangle2D nearestScreenBounds = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds).filter(hasNeighbourOnTop).max(maxYComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentTransformableScreen.getTransformedBounds();
		currentTransformableScreen.setTransformedBoundsMinY(calculateTransformedBoundsMinY(currentTransformableScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentTransformableScreen.getTransformedBounds();
		boolean newBoundsIntersect = TransformableScreen.getTransformableScreens().stream().filter(screen -> !screen.equals(currentTransformableScreen)).map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining top side transformed bounds for screen: " + currentTransformableScreen);
			currentTransformableScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private static double calculateTransformedBoundsMinY(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return 0;

		Rectangle2D screenBounds = transformableScreen.getRealBounds();

		double sharedMinY = screenBounds.getMinY() - (screenBounds.getMinY() - nearestScreenBounds.getMaxY()) / 2;
		boolean shareUnreachableAreaVertically = canShareUnreachableVerticalArea(screenBounds, nearestScreenBounds);
		boolean resultingTransformationsIntersect = willResultingMinYTransformationCauseIntersections(transformableScreen, nearestScreenBounds, sharedMinY);

		return shareUnreachableAreaVertically && !resultingTransformationsIntersect ? sharedMinY : nearestScreenBounds.getMaxY();
	}

	private static boolean willResultingMinYTransformationCauseIntersections(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds, double sharedMinY) {
		Rectangle2D newTransformedBounds = new Rectangle2D(transformableScreen.getTransformedBounds().getMinX(), sharedMinY, transformableScreen.getTransformedBounds().getWidth(),
														   transformableScreen.getTransformedBounds().getMaxY() - sharedMinY);
		boolean screenTransformationIntersects = TransformableScreen.getTransformableScreens().stream().filter(screen1 -> !transformableScreen.equals(screen1))
				.map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);

		Rectangle2D nearestScreenNewBounds = new Rectangle2D(nearestScreenBounds.getMinX(), nearestScreenBounds.getMinY(),
															 nearestScreenBounds.getWidth(), sharedMinY - nearestScreenBounds.getMinY());
		boolean nearestScreenTransformationIntersects = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds)
				.filter(bounds -> !bounds.equals(nearestScreenBounds)).anyMatch(nearestScreenNewBounds::intersects);

		return screenTransformationIntersects || nearestScreenTransformationIntersects;
	}

	private static void calculateTransformedBoundsBottomSide(Pair<TransformableScreen, ScreenSides> screenSide) {
		TransformableScreen currentTransformableScreen = screenSide.getKey();
		Predicate<Rectangle2D> hasNeighbourOnBottom = currentTransformableScreen::isNeighbouringOnBottomSide;
		Comparator<Rectangle2D> minYComparator = Comparator.comparingDouble(Rectangle2D::getMinY);
		Rectangle2D nearestScreenBounds = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds).filter(hasNeighbourOnBottom).min(minYComparator).orElse(null);

		Rectangle2D prevTransformedBounds = currentTransformableScreen.getTransformedBounds();
		currentTransformableScreen.setTransformedBoundsMaxY(calculateTransformedBoundsMaxY(currentTransformableScreen, nearestScreenBounds));
		Rectangle2D newTransformedBounds = currentTransformableScreen.getTransformedBounds();
		boolean newBoundsIntersect = TransformableScreen.getTransformableScreens().stream().filter(screen -> !screen.equals(currentTransformableScreen)).map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);
		if (newBoundsIntersect) {
			System.err.println("Intersection occurred while determining bottom side transformed bounds for screen: " + currentTransformableScreen);
			currentTransformableScreen.setTransformedBounds(prevTransformedBounds);
		}
	}

	private static double calculateTransformedBoundsMaxY(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds) {
		if (nearestScreenBounds == null)
			return GlobalVariables.screenHeight;

		Rectangle2D screenBounds = transformableScreen.getRealBounds();

		double sharedMaxY = screenBounds.getMaxY() + (nearestScreenBounds.getMinY() - screenBounds.getMaxY()) / 2;
		boolean shareUnreachableAreaVertically = canShareUnreachableVerticalArea(screenBounds, nearestScreenBounds);
		boolean resultingTransformationsIntersect = willResultingMaxYTransformationCauseIntersections(transformableScreen, nearestScreenBounds, sharedMaxY);

		return shareUnreachableAreaVertically && !resultingTransformationsIntersect ? sharedMaxY : nearestScreenBounds.getMinY();
	}

	private static boolean willResultingMaxYTransformationCauseIntersections(TransformableScreen transformableScreen, Rectangle2D nearestScreenBounds, double sharedMaxY) {
		Rectangle2D newTransformedBounds = new Rectangle2D(transformableScreen.getTransformedBounds().getMinX(), transformableScreen.getTransformedBounds().getMinY(),
														   transformableScreen.getTransformedBounds().getWidth(), sharedMaxY - transformableScreen.getTransformedBounds().getMinY());
		boolean screenTransformationIntersects = TransformableScreen.getTransformableScreens().stream().filter(screen1 -> !transformableScreen.equals(screen1))
				.map(TransformableScreen::getTransformedBounds).anyMatch(newTransformedBounds::intersects);

		Rectangle2D nearestScreenNewBounds = new Rectangle2D(nearestScreenBounds.getMinX(), sharedMaxY,
															 nearestScreenBounds.getWidth(), nearestScreenBounds.getMaxY() - sharedMaxY);
		boolean nearestScreenTransformationIntersects = TransformableScreen.getTransformableScreens().stream().map(TransformableScreen::getTransformedBounds)
				.filter(bounds -> !bounds.equals(nearestScreenBounds)).anyMatch(nearestScreenNewBounds::intersects);

		return screenTransformationIntersects || nearestScreenTransformationIntersects;
	}

	private static boolean canShareUnreachableHorizontalArea(Rectangle2D bounds1, Rectangle2D bounds2) {
		return (bounds1.getMinY() >= bounds2.getMinY() && bounds1.getMaxY() <= bounds2.getMaxY()) ||
			   (bounds1.getMinY() <= bounds2.getMinY() && bounds1.getMaxY() >= bounds2.getMaxY());
	}

	private static boolean canShareUnreachableVerticalArea(Rectangle2D bounds1, Rectangle2D bounds2) {
		return (bounds1.getMinX() >= bounds2.getMinX() && bounds1.getMaxX() <= bounds2.getMaxX()) ||
			   (bounds1.getMinX() <= bounds2.getMinX() && bounds1.getMaxX() >= bounds2.getMaxX());
	}
}
