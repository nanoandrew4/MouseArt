package iart.color_schemes.wheel_scheme;

/**
 * Follows the same principle as the WheelScheme, but uses grayscale instead of actual color when creating the
 * wheel.
 */
public class GrayscaleWheelScheme extends WheelScheme {
	public GrayscaleWheelScheme() {
		grayscale = true;
		inverted = false;
	}
}
