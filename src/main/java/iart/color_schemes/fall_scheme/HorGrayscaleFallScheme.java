package iart.color_schemes.fall_scheme;

/**
 * Effect created by this color scheme is as follows. Leftmost part of the screen has lowest brightness, and rightmost
 * has highest brightness. Saturation is fixed at 0, which gives the grayscale gradient.
 */
public class HorGrayscaleFallScheme extends FallScheme {
	public HorGrayscaleFallScheme() {
		grayscale = true;
		vertical = false;
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
