package iart.color_schemes.wheel_scheme;

/**
 * Effect created by this scheme is as follows. The centremost point of the screen is black, and the edges are white.
 * The color used to draw any graphical objects is a blend of white and black, depending on the distance from the
 * centre of the screen to the mouse pointer position.
 */
public class InvGrayscaleWheelScheme extends WheelScheme {
	public InvGrayscaleWheelScheme() {
		grayscale = inverted = true;
	}

	@Override
	public void registerSuperScheme() {
	}
}
