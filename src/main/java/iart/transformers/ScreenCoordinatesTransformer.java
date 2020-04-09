package iart.transformers;

import iart.Main;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScreenCoordinatesTransformer {

	private static ScreenCoordinatesTransformer instance;

	private Rectangle2D[][] virtualScreens;

	private HashMap<Rectangle2D, TransformerFunctionPair> screenTransformations;

	static {
		instance = new ScreenCoordinatesTransformer();
	}

	private ScreenCoordinatesTransformer() {
		calculateVirtualScreens();
		calculateTransformationsForScreens();
	}

	public static ScreenCoordinatesTransformer getInstance() {
		return instance;
	}

	public static ScreenCoordinatesTransformer getNewInstance() {
		return (instance = new ScreenCoordinatesTransformer());
	}

	public Optional<Rectangle2D> getVirtualScreenForPoint(Point p) {
		for (int j = 0; j < virtualScreens.length; j++)
			for (int i = 0; i < virtualScreens[j].length; i++)
				if (virtualScreens[i][j].contains(p.x, p.y))
					return Optional.of(virtualScreens[i][j]);
		return Optional.empty();
	}

	private Optional<Rectangle2D> getRealScreenForPoint(Point p) {
		return Screen.getScreens().stream().map(Screen::getBounds).filter(bounds -> bounds.contains(p.x, p.y)).findFirst();
	}

	public void transformPoint(Point p) {
		TransformerFunctionPair transformerFunctionPair = screenTransformations.get(getRealScreenForPoint(p).orElse(null));
		if (transformerFunctionPair != null)
			transformerFunctionPair.transform(p);
	}

	public void calculateTransformationsForScreens() {
		screenTransformations = new HashMap<>();

		Map<Rectangle2D, CoordinateTransformationInfo> intersectingScreens = new HashMap<>();
		List<Rectangle2D> virtualScreens = collectVirtualScreens();
		Screen.getScreens().stream().map(Screen::getBounds).forEach(realScreenBounds -> {
			virtualScreens.forEach(virtualScreenBounds -> {
				if (realScreenBounds.intersects(virtualScreenBounds) && !realScreenBounds.equals(virtualScreenBounds)) {
					CoordinateTransformationInfo transformationInfo = intersectingScreens.get(realScreenBounds);
					if (transformationInfo == null) {
						boolean requiresXAxisTransformation = realScreenBounds.getMinX() != virtualScreenBounds.getMinX() || realScreenBounds.getMaxX() != virtualScreenBounds.getMaxX();
						boolean requiresYAxisTransformation = realScreenBounds.getMinY() != virtualScreenBounds.getMinY() || realScreenBounds.getMaxY() != virtualScreenBounds.getMaxY();
						if (requiresXAxisTransformation && !requiresYAxisTransformation)
							transformationInfo = new CoordinateTransformationInfo(TransformationAxis.X);
						else if (requiresYAxisTransformation && !requiresXAxisTransformation)
							transformationInfo = new CoordinateTransformationInfo(TransformationAxis.Y);
						else
							transformationInfo = new CoordinateTransformationInfo(TransformationAxis.UNKNOWN);
					} else {
						boolean isTransformingXAxis = transformationInfo.getAxis() == TransformationAxis.X;
						double maxValue = Math.max(transformationInfo.getCombinedIntersectingVirtualScreensWidthOrHeight(), isTransformingXAxis ? virtualScreenBounds.getMaxX() : virtualScreenBounds.getMaxY());
						transformationInfo.setCombinedIntersectingVirtualScreensWidthOrHeight(maxValue);
					}
					intersectingScreens.put(realScreenBounds, transformationInfo);
				}
			});
		});

		intersectingScreens.forEach((realScreen, transformationInfo) -> {
			Function<Integer, Integer> transformX = x -> x, transformY = y -> y;

			if (transformationInfo.getAxis() == TransformationAxis.X)
				transformX = (x) -> (int) (((x - realScreen.getMinX()) / (realScreen.getWidth())) * transformationInfo.getCombinedIntersectingVirtualScreensWidthOrHeight());
			else if (transformationInfo.getAxis() == TransformationAxis.Y)
				transformY = (y) -> (int) (((y - realScreen.getMinY()) / (realScreen.getHeight()) * transformationInfo.getCombinedIntersectingVirtualScreensWidthOrHeight()));

			screenTransformations.put(realScreen, new TransformerFunctionPair(transformX, transformY));
		});
	}

	private List<Rectangle2D> collectVirtualScreens() {
		List<Rectangle2D> collectedVirtualScreens = new LinkedList<>();
		for (Rectangle2D[] virtualScreen : virtualScreens)
			collectedVirtualScreens.addAll(Arrays.asList(virtualScreen));
		return collectedVirtualScreens;
	}

	private void calculateVirtualScreens() {
		int screensHorizontally = (int) getMaxScreensOnXAxis();
		int screensVertically = (int) getMaxScreensOnYAxis();

		if (screensHorizontally + screensVertically > 2 && screensHorizontally == 1)
			screensHorizontally++;
		if (screensHorizontally + screensVertically > 2 && screensVertically == 1)
			screensVertically++;

		virtualScreens = new Rectangle2D[screensHorizontally][screensVertically];

		final double virtualScreenWidth = (double) Main.screenWidth / screensHorizontally;
		final double virtualScreenHeight = (double) Main.screenHeight / screensVertically;
		for (int j = 0; j < screensVertically; j++)
			for (int i = 0; i < screensHorizontally; i++)
				virtualScreens[i][j] = new Rectangle2D(virtualScreenWidth * i, virtualScreenHeight * j, virtualScreenWidth, virtualScreenHeight);
	}

	private long getMaxScreensOnXAxis() {
		List<Rectangle2D> realScreenBounds = Screen.getScreens().stream().map(Screen::getBounds).collect(Collectors.toList());
		Set<Double> screenHeights = new HashSet<>();
		realScreenBounds.forEach(rectangle2D -> screenHeights.add(rectangle2D.getMinY()));

		long maxScreensHorizontally = 1;
		for (Double screenHeight : screenHeights)
			maxScreensHorizontally = Math.max(maxScreensHorizontally, realScreenBounds.stream().filter(rectangle2D -> rectangle2D.getMinY() == screenHeight).count());
		return maxScreensHorizontally;
	}

	private long getMaxScreensOnYAxis() {
		List<Rectangle2D> realScreenBounds = Screen.getScreens().stream().map(Screen::getBounds).collect(Collectors.toList());
		Set<Double> screenWidths = new HashSet<>();
		realScreenBounds.forEach(rectangle2D -> screenWidths.add(rectangle2D.getMinX()));

		long maxScreensVertically = 1;
		for (Double screenWidth : screenWidths)
			maxScreensVertically = Math.max(maxScreensVertically, realScreenBounds.stream().filter(rectangle2D -> rectangle2D.getMinX() == screenWidth).count());
		return maxScreensVertically;
	}
}
