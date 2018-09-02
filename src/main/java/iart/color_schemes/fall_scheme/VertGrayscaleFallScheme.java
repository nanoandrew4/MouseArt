package iart.color_schemes.fall_scheme;

/**
 * Effect created by this color scheme is as follows. Topmost part of the screen has lowest brightness, and bottommost
 * has highest brightness. Saturation is fixed at 0, which gives the grayscale gradient.
 */
public class VertGrayscaleFallScheme extends FallScheme {
	public VertGrayscaleFallScheme() {
		grayscale = vertical = true;
	}

	@Override
	public void registerSuperScheme() {
	}

	@Override
	public void startColorScheme() {
	}

	@Override
	public void stopColorScheme() {
	}
}
