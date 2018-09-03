package iart.color_schemes.wheel_scheme;

/**
 * Effect created by this scheme is as follows. The centremost point of the screen is white, and the edges are varying
 * colors. The color used to draw any graphical objects is a blend of white and the designated color at the border of
 * the screen, at the angle between the centre of the screen and the mouse pointer. The angle is fed to the HSB color
 * model, and a hue is determined for the geometric shape to be drawn. Since the colors are obtained through angles,
 * the final effect is that of a color wheel, since it is as if the color spectrum had been stretched around a circle,
 * and blended with white depending on the distance of the pointer from the centre.
 */
public class ColorWheelScheme extends WheelScheme {
	public ColorWheelScheme() {
		grayscale = inverted = false;
	}

	@Override
	public void registerSuperScheme() {
	}
}
