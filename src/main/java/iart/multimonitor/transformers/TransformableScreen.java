package iart.multimonitor.transformers;

import javafx.geometry.Rectangle2D;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TransformableScreen {
	private static List<TransformableScreen> transformableScreens;

	private final TransformerFunctionPair transformerFunctionPair;
	private Rectangle2D realBounds;
	private Rectangle2D transformedBounds;

	private TransformableScreen() {
		transformerFunctionPair = new TransformerFunctionPair();
	}

	public TransformableScreen(javafx.stage.Screen screen) {
		this(screen.getBounds());
	}

	public TransformableScreen(Rectangle2D bounds) {
		this(bounds, bounds);
	}

	public TransformableScreen(Rectangle2D realBounds, Rectangle2D transformedBounds) {
		this();
		this.realBounds = realBounds;
		this.transformedBounds = transformedBounds;
	}

	public static void generateScreens() {
		transformableScreens = javafx.stage.Screen.getScreens().stream().map(TransformableScreen::new).collect(Collectors.toList());
	}

	public static List<TransformableScreen> getTransformableScreens() {
		return transformableScreens;
	}

	public static TransformableScreen getScreenForTransformedPoint(Point p) {
		return transformableScreens.stream().filter(screen -> screen.getTransformedBounds().contains(p.x, p.y)).findFirst().orElse(null);
	}

	public static void transformPoint(Point p) {
		transformableScreens.stream().filter(screen -> screen.getRealBounds().contains(p.x, p.y)).findFirst().ifPresent(screen -> screen.getTransformer().transform(p));
	}

	public void setTransformedBoundsMinX(double minX) {
		transformedBounds = new Rectangle2D(minX, transformedBounds.getMinY(), transformedBounds.getMaxX() - minX, transformedBounds.getHeight());
		updateTransformationX();
	}

	public void setTransformedBoundsMaxX(double maxX) {
		transformedBounds = new Rectangle2D(transformedBounds.getMinX(), transformedBounds.getMinY(), maxX - transformedBounds.getMinX(), transformedBounds.getHeight());
		updateTransformationX();
	}

	public void setTransformedBoundsMinY(double minY) {
		transformedBounds = new Rectangle2D(transformedBounds.getMinX(), minY, transformedBounds.getWidth(), transformedBounds.getMaxY() - minY);
		updateTransformationY();
	}

	public void setTransformedBoundsMaxY(double maxY) {
		transformedBounds = new Rectangle2D(transformedBounds.getMinX(), transformedBounds.getMinY(), transformedBounds.getWidth(), maxY - transformedBounds.getMinY());
		updateTransformationY();
	}

	private void updateTransformationX() {
		Function<Integer, Integer> transformX = x -> (int) (((x - realBounds.getMinX()) / realBounds.getWidth()) * transformedBounds.getWidth() + transformedBounds.getMinX());
		transformerFunctionPair.setTransformX(transformX);
	}

	private void updateTransformationY() {
		Function<Integer, Integer> transformY = y -> (int) (((y - realBounds.getMinY()) / realBounds.getHeight()) * transformedBounds.getHeight() + transformedBounds.getMinY());
		transformerFunctionPair.setTransformY(transformY);
	}

	public TransformerFunctionPair getTransformer() {
		return transformerFunctionPair;
	}

	public Rectangle2D getRealBounds() {
		return realBounds;
	}

	public Rectangle2D getTransformedBounds() {
		return transformedBounds;
	}

	public void setTransformedBounds(Rectangle2D transformedBounds) {
		this.transformedBounds = transformedBounds;
	}

	public boolean isNeighbouringDirectlyOnRightSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxX() == potentialNeighbourBounds.getMinX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	public boolean isNeighbouringOnRightSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxX() < potentialNeighbourBounds.getMinX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	public boolean isNeighbouringDirectlyOnLeftSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinX() == potentialNeighbourBounds.getMaxX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY()));
	}

	public boolean isNeighbouringOnLeftSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinX() > potentialNeighbourBounds.getMaxX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY()));
	}

	public boolean isNeighbouringDirectlyOnTopSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinY() == potentialNeighbourBounds.getMaxY() &&
			   ((this.realBounds.getMinX() < potentialNeighbourBounds.getMaxX() && this.realBounds.getMinX() >= potentialNeighbourBounds.getMinX()) ||
				(this.realBounds.getMaxX() > potentialNeighbourBounds.getMinX() && this.realBounds.getMaxX() < potentialNeighbourBounds.getMaxX())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	public boolean isNeighbouringOnTopSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinY() > potentialNeighbourBounds.getMaxY() &&
			   ((this.realBounds.getMinX() < potentialNeighbourBounds.getMaxX() && this.realBounds.getMinX() >= potentialNeighbourBounds.getMinX()) ||
				(this.realBounds.getMaxX() > potentialNeighbourBounds.getMinX() && this.realBounds.getMaxX() < potentialNeighbourBounds.getMaxX())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	public boolean isNeighbouringDirectlyOnBottomSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxY() == potentialNeighbourBounds.getMinY() &&
			   ((this.realBounds.getMinX() < potentialNeighbourBounds.getMaxX() && this.realBounds.getMinX() >= potentialNeighbourBounds.getMinX()) ||
				(this.realBounds.getMaxX() > potentialNeighbourBounds.getMinX() && this.realBounds.getMaxX() < potentialNeighbourBounds.getMaxX())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	public boolean isNeighbouringOnBottomSide(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxY() < potentialNeighbourBounds.getMinY() &&
			   ((this.realBounds.getMinX() < potentialNeighbourBounds.getMaxX() && this.realBounds.getMinX() >= potentialNeighbourBounds.getMinX()) ||
				(this.realBounds.getMaxX() > potentialNeighbourBounds.getMinX() && this.realBounds.getMaxX() < potentialNeighbourBounds.getMaxX())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TransformableScreen transformableScreen = (TransformableScreen) o;
		return realBounds.equals(transformableScreen.realBounds) && transformedBounds.equals(transformableScreen.transformedBounds);
	}

	@Override
	public int hashCode() {
		return Objects.hash(realBounds, transformedBounds);
	}

	@Override
	public String toString() {
		return "Screen{ realBounds=" + realBounds + ", transformedBounds=" + transformedBounds + '}';
	}
}
