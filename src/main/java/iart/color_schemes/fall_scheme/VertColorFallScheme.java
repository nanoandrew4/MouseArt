package iart.color_schemes.fall_scheme;

/**
 * Effect created by this color scheme is as follows. Topmost part of the screen has highest saturation, and bottommost
 * has lowest saturation. Brightness is fixed at 1, so that the gradient goes from the current hue to white.
 */
public class VertColorFallScheme extends FallScheme {
	public VertColorFallScheme() {
		grayscale = false;
		vertical = true;
	}

	@Override
	public void registerSuperScheme() {
	}
}
