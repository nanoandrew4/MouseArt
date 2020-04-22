package iart.multimonitor.transformers;

public enum ScreenSides {
	LEFT, RIGHT, TOP, BOTTOM;

	public boolean isHorizontalSide() {
		return this == LEFT || this == RIGHT;
	}
}
