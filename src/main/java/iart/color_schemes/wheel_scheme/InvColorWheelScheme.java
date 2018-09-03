package iart.color_schemes.wheel_scheme;

/**
 * Effect created by this scheme is as follows. The centremost point of the screen is a color, and the edges are white.
 * The color used to draw any graphical objects is a blend of white and the designated color at near the centre of the
 * screen, at the angle between the centre of the screen and the mouse pointer. The angle is fed to the HSB color
 * model, and a hue is determined for the geometric shape to be drawn. Since the colors are obtained through angles,
 * the final effect is that of an inverted color wheel, since it is as if the color spectrum had been stretched around
 * an infinitely small circle at the centre of the screen, and the color at the given angle is blended with white,
 * depending on the distance from the centre of the screen to the mouse pointer.
 */
public class InvColorWheelScheme extends WheelScheme {
	public InvColorWheelScheme() {
		grayscale = false;
		inverted = true;
	}

	@Override
	public void registerSuperScheme() {
	}
}
