package mouseart;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Records all mouse movements and submits operations to be carried out by ImageDrawer.
 */
public class ImageRecorder extends Thread {

	private Point prevLocation = MouseInfo.getPointerInfo().getLocation();
	private ImageDrawer im;

	static boolean mousePressed, mouseReleased;

	ImageRecorder(ImageDrawer im) {
		this.im = im;
		this.setDaemon(true);
		this.setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public void run() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		GlobalScreen.addNativeMouseListener(new MouseHook());

		long start = System.currentTimeMillis();
		long diff;

		while (MouseArt.state != mouseart.State.STOPPED) {
			Point location = MouseInfo.getPointerInfo().getLocation();

			/*
			 * If the mouse has moved, draw a line between previous position and current position.
			 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
			 * cube root of the time elapsed until the mouse was moved again.
			 */
			if (MouseArt.state == mouseart.State.RECORDING && !prevLocation.equals(location)) {
				if ((diff = System.currentTimeMillis() - start) > 3000) {
					im.addOperation((int) (prevLocation.getX() - Math.cbrt(diff)), (int) prevLocation.getY(),
							(int) (prevLocation.getX() + Math.cbrt(diff)), (int) prevLocation.getY(), 'c');
				}
				if (mouseReleased) {
					// draw filled circle of white!
				}
				start = System.currentTimeMillis();
				im.addOperation(location.x, location.y, prevLocation.x, prevLocation.y, 'l');
			}
			prevLocation = MouseInfo.getPointerInfo().getLocation();

			try {
				Thread.sleep(33);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
