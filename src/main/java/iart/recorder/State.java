package iart.recorder;

/**
 * Set of states the ImageRecorder can be in. They are used to control the flow of the program.
 * <p>
 * PRE_RECORDING indicates that the recording process is being set up, but the listeners should not start processing
 * input events yet, since all necessary parts may not be set up and calibrated properly at the present time.
 * <p>
 * RECORDING indicates the program is recording, which makes ImageRecorder to submit Operation objects to ImageDrawer,
 * to be drawn on to the canvas and the screen.
 * <p>
 * PAUSED indicates the program is not tracking the cursor, until resumed.
 * <p>
 * STOPPED indicates the program has stopped tracking the cursor and is finalizing the image, to save it and start anew.
 */
public enum State {
	PRE_RECORDING, RECORDING, CALIBRATING, PAUSED, STOPPED
}