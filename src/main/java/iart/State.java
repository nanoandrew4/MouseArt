package iart;

/**
 * Set of states the ImageRecorder can be in. They are used to control the flow of the program.
 *
 * RECORDING indicates the program is recording, which makes ImageRecorder to submit Operation objects to ImageDrawer,
 * to be drawn on to the canvas and the screen.
 *
 * PAUSED indicates the program is not tracking the cursor, until resumed.
 *
 * STOPPED indicates the program has stopped tracking the cursor and is finalizing the image, to save it and start anew.
 */
public enum State {
	RECORDING, PAUSED, STOPPED
}