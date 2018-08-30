package iart.color_schemes.wheel_scheme;

/**
 * Color scheme that makes the canvas emulate a color wheel. The cursor position is used to calculate the HSV color
 * that the shape should be drawn with.
 */
public class ColorWheelScheme extends WheelScheme {
	public ColorWheelScheme() {
		grayscale = inverted = false;
	}

	@Override
	public void registerSuperScheme() {
	}
}
