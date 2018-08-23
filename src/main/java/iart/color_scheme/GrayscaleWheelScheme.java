package iart.color_scheme;

/**
 * Follows the same principle as the ColorWheelScheme, but uses grayscale instead of actual color when creating the
 * wheel.
 */
public class GrayscaleWheelScheme extends ColorWheelScheme {
	public GrayscaleWheelScheme() {
		grayscale = true;
	}
}
