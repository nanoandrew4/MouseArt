package mouseart.geometry;

public class Circle extends Geometry {
	public int centerX, centerY, radius;
	public boolean fill;

	public Circle(int centerX, int centerY, int radius, boolean fill) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.fill = fill;
	}

	public Circle(int centerX, int centerY, int radius, boolean fill, int alpha) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.radius = radius;
		this.fill = fill;
		this.alpha = alpha;
	}
}