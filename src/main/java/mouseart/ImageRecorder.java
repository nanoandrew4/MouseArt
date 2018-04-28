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

	static boolean mousePressed;

	ImageRecorder(ImageDrawer im) {
		this.im = im;
		this.setDaemon(true);
		this.setPriority(Thread.MAX_PRIORITY);
	}

	@Override
	public void run() {
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			e.printStackTrace();
		}

		GlobalScreen.addNativeMouseListener(new MouseHook());

		long diff, start = System.currentTimeMillis();

		while (MouseArt.state != mouseart.State.STOPPED) {
			Point location = MouseInfo.getPointerInfo().getLocation();

			/*
			 * If the mouse has moved, draw a line between previous position and current position.
			 * If the mouse was stopped for longer than three seconds, draw a circle with a radius proportional to the
			 * cube root of the time elapsed until the mouse was moved again.
			 */
			if (MouseArt.state == mouseart.State.RECORDING) {
				if (mousePressed) {
					im.addCircleOp(prevLocation.x, prevLocation.y, MouseArt.rand.nextInt(50) + 25, true, 255 - MouseArt.rand.nextInt(25));
					mousePressed = false;
				}
				if (!prevLocation.equals(location)) {
					if ((diff = System.currentTimeMillis() - start) > 3000) {
						int radius = (int) Math.sqrt(diff);
						im.addCircleOp(prevLocation.x, prevLocation.y, radius, false);
						im.addCircleOp(prevLocation.x, prevLocation.y, radius / 10, true);
					}
					start = System.currentTimeMillis();
					im.addLineOp(location.x, location.y, prevLocation.x, prevLocation.y);
				}
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
