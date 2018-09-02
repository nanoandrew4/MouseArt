package iart.color_schemes.fall_scheme;

/**
 * Effect created by this color scheme is as follows. Leftmost part of the screen has highest saturation, rightmost has
 * lowest saturation level. Brightness is fixed at 1, so that the gradient goes from the current hue to white.
 */
public class HorColorFallScheme extends FallScheme {
	public HorColorFallScheme() {
		vertical = grayscale = false;
	}

	@Override
	public void registerSuperScheme() {
	}
}
