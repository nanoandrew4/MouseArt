package iart.draw;

/**
 * Objects drawn in Main. Each one, when passed to the ColorScheme class, will return the appropriate color for
 * the specific object being drawn.
 */
public enum DrawEvent {
	MOUSE_MOVE, KEYSTROKE, MOVE_INNER_CIRCLE, MOVE_OUTER_CIRCLE, LMOUSE_PRESS, BACKGROUND
}
