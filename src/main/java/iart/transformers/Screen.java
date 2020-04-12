package iart.transformers;

import javafx.geometry.Rectangle2D;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Screen {
	private final TransformerFunctionPair transformerFunctionPair;
	private Rectangle2D realBounds;
	private Rectangle2D transformedBounds;

	private Screen() {
		transformerFunctionPair = new TransformerFunctionPair();
	}

	public Screen(javafx.stage.Screen screen) {
		this(screen.getBounds());
	}

	public Screen(Rectangle2D bounds) {
		this(bounds, bounds);
	}

	public Screen(Rectangle2D realBounds, Rectangle2D transformedBounds) {
		this();
		this.realBounds = realBounds;
		this.transformedBounds = transformedBounds;
	}

	public static List<Screen> generateScreens() {
		return javafx.stage.Screen.getScreens().stream().map(Screen::new).collect(Collectors.toList());
	}

	public void setTransformedBoundsMinX(double minX) {
		transformedBounds = new Rectangle2D(minX, transformedBounds.getMinY(), transformedBounds.getMaxX() - minX, transformedBounds.getHeight());
		updateTransformationX();
	}

	public void setTransformedBoundsMaxX(double maxX) {
		transformedBounds = new Rectangle2D(transformedBounds.getMinX(), transformedBounds.getMinY(), maxX - transformedBounds.getMinX(), transformedBounds.getHeight());
		updateTransformationX();
	}

	private void updateTransformationX() {
		Function<Integer, Integer> transformX = x -> (int) (((x - realBounds.getMinX()) / realBounds.getWidth()) * transformedBounds.getWidth() + transformedBounds.getMinX());
		transformerFunctionPair.setTransformX(transformX);
	}

	private void updateTransformationY() {
		Function<Integer, Integer> transformY = y -> y;
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

	public boolean contains(Rectangle2D rect) {
		return realBounds.contains(rect);
	}

	public boolean intersects(Rectangle2D rect) {
		return realBounds.intersects(rect);
	}

	protected boolean hasNeighbourDirectlyToRight(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxX() == potentialNeighbourBounds.getMinX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	protected boolean hasNeighbourToRight(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMaxX() < potentialNeighbourBounds.getMinX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY())); // TODO: POSSIBLE REFACTOR, LAST TWO LINES ARE THE SAME
	}

	protected boolean hasNeighbourDirectlyToLeft(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinX() == potentialNeighbourBounds.getMaxX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY()));
	}

	protected boolean hasNeighbourToLeft(Rectangle2D potentialNeighbourBounds) {
		return this.realBounds.getMinX() > potentialNeighbourBounds.getMaxX() &&
			   ((this.realBounds.getMinY() < potentialNeighbourBounds.getMaxY() && this.realBounds.getMinY() >= potentialNeighbourBounds.getMinY()) ||
				(this.realBounds.getMaxY() > potentialNeighbourBounds.getMinY() && this.realBounds.getMaxY() < potentialNeighbourBounds.getMaxY()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Screen screen = (Screen) o;
		return realBounds.equals(screen.realBounds) && transformedBounds.equals(screen.transformedBounds);
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
