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

	private List<Rectangle2D> virtualScreens;

	private HashMap<Rectangle2D, TransformerFunctionPair> screenTransformations;

	static {
		instance = new ScreenCoordinatesTransformer();
	}

	private ScreenCoordinatesTransformer() {
		createVirtualScreens();
		calculateTransformationsForScreens();
	}

	public static ScreenCoordinatesTransformer getInstance() {
		return instance;
	}

	public static void invalidateCurrentInstance() {
		instance = new ScreenCoordinatesTransformer();
	}

	private Optional<Rectangle2D> getRealScreenForPoint(Point p) {
		return Screen.getScreens().stream().map(Screen::getBounds).filter(bounds -> bounds.contains(p.x, p.y)).findFirst();
	}

	public void transformPoint(Point p) {
		TransformerFunctionPair transformerFunctionPair = screenTransformations.get(getRealScreenForPoint(p).orElse(null));
		if (transformerFunctionPair != null)
			transformerFunctionPair.transform(p);
	}

	private CoordinateTransformationInfo createTransformationInfo(Rectangle2D realScreenBounds, Rectangle2D virtualScreenBounds) {
		boolean requiresXAxisTransformation = realScreenBounds.getMinX() != virtualScreenBounds.getMinX() || realScreenBounds.getMaxX() != virtualScreenBounds.getMaxX();
		boolean requiresYAxisTransformation = realScreenBounds.getMinY() != virtualScreenBounds.getMinY() || realScreenBounds.getMaxY() != virtualScreenBounds.getMaxY();
		if (requiresXAxisTransformation && !requiresYAxisTransformation)
			return new CoordinateTransformationInfo(TransformationAxis.X);
		else if (requiresYAxisTransformation && !requiresXAxisTransformation)
			return new CoordinateTransformationInfo(TransformationAxis.Y);
		else
			return new CoordinateTransformationInfo(TransformationAxis.UNKNOWN);
	}

	public void calculateTransformationsForScreens() {
		screenTransformations = new HashMap<>();

		Map<Rectangle2D, CoordinateTransformationInfo> transformationInfoMap = generateTransformationInfo();
		calculateTransformations(transformationInfoMap);
	}

	private void calculateTransformations(Map<Rectangle2D, CoordinateTransformationInfo> transformationInfoMap) {
		transformationInfoMap.forEach((realScreen, transformationInfo) -> {
			Function<Integer, Integer> transformX = x -> x, transformY = y -> y;

			if (transformationInfo.getAxis() == TransformationAxis.X)
				transformX = (x) -> (int) (((x - realScreen.getMinX()) / (realScreen.getWidth())) * transformationInfo.getIntersectingVirtualScreensLength());
			else if (transformationInfo.getAxis() == TransformationAxis.Y)
				transformY = (y) -> (int) (((y - realScreen.getMinY()) / (realScreen.getHeight()) * transformationInfo.getIntersectingVirtualScreensLength()));

			screenTransformations.put(realScreen, new TransformerFunctionPair(transformX, transformY));
		});
	}

	private Map<Rectangle2D, CoordinateTransformationInfo> generateTransformationInfo() {
		Map<Rectangle2D, CoordinateTransformationInfo> transformationInfoMap = new HashMap<>();

		Screen.getScreens().stream().map(Screen::getBounds).forEach(realScreenBounds -> {
			virtualScreens.forEach(virtualScreenBounds -> {
				if (realScreenBounds.intersects(virtualScreenBounds) && !realScreenBounds.equals(virtualScreenBounds)) {
					CoordinateTransformationInfo transformationInfo = transformationInfoMap.get(realScreenBounds);
					if (transformationInfo == null) {
						transformationInfo = createTransformationInfo(realScreenBounds, virtualScreenBounds);
					} else {
						boolean isTransformingXAxis = transformationInfo.getAxis() == TransformationAxis.X;
						double maxValue = Math.max(transformationInfo.getIntersectingVirtualScreensLength(), isTransformingXAxis ? virtualScreenBounds.getMaxX() : virtualScreenBounds.getMaxY());
						transformationInfo.setIntersectingVirtualScreensLength(maxValue);
					}
					transformationInfoMap.put(realScreenBounds, transformationInfo);
				}
			});
		});
		return transformationInfoMap;
	}

	private void createVirtualScreens() {
		int screensHorizontally = (int) getMaxNumOfScreensOnXAxis();
		int screensVertically = (int) getMaxNumOfScreensOnYAxis();

		if (screensHorizontally + screensVertically > 2 && screensHorizontally == 1)
			screensHorizontally++;
		if (screensHorizontally + screensVertically > 2 && screensVertically == 1)
			screensVertically++;

		virtualScreens = new LinkedList<>();

		final double virtualScreenWidth = (double) Main.screenWidth / screensHorizontally;
		final double virtualScreenHeight = (double) Main.screenHeight / screensVertically;
		for (int j = 0; j < screensVertically; j++)
			for (int i = 0; i < screensHorizontally; i++)
				virtualScreens.add(new Rectangle2D(virtualScreenWidth * i, virtualScreenHeight * j, virtualScreenWidth, virtualScreenHeight));
	}

	private long getMaxNumOfScreensOnXAxis() {
		List<Rectangle2D> realScreenBounds = Screen.getScreens().stream().map(Screen::getBounds).collect(Collectors.toList());
		Set<Double> screenHeights = new HashSet<>();
		realScreenBounds.forEach(rectangle2D -> screenHeights.add(rectangle2D.getMinY()));

		long maxScreensHorizontally = 1;
		for (Double screenHeight : screenHeights)
			maxScreensHorizontally = Math.max(maxScreensHorizontally, realScreenBounds.stream().filter(rectangle2D -> rectangle2D.getMinY() == screenHeight).count());
		return maxScreensHorizontally;
	}

	private long getMaxNumOfScreensOnYAxis() {
		List<Rectangle2D> realScreenBounds = Screen.getScreens().stream().map(Screen::getBounds).collect(Collectors.toList());
		Set<Double> screenWidths = new HashSet<>();
		realScreenBounds.forEach(rectangle2D -> screenWidths.add(rectangle2D.getMinX()));

		long maxScreensVertically = 1;
		for (Double screenWidth : screenWidths)
			maxScreensVertically = Math.max(maxScreensVertically, realScreenBounds.stream().filter(rectangle2D -> rectangle2D.getMinX() == screenWidth).count());
		return maxScreensVertically;
	}
}
