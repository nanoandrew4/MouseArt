package mouseart.geometry;

public class Line extends Geometry {
	public int startX, startY, endX, endY;

	public Line(int startX, int startY, int endX, int endY) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
	}
}
