package mouseart;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

/**
 * Draws all operations submitted by ImageRecorder to the BufferedImage and updates the UI preview periodically.
 */
public class ImageDrawer extends Thread {

	private BufferedImage bi;
	private ArrayDeque<Operation> operations = new ArrayDeque<>();

	private ImageView imageView;

	ImageDrawer(int screenWidth, int screenHeight, ImageView imageView) {
		this.setDaemon(true);
		this.setPriority(Thread.NORM_PRIORITY);
		this.imageView = imageView;

		bi = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

		// Fill in background with white (default is black)
		Color bgColor = new Color(255, 255, 255);
		for (int y = 0; y < screenHeight; y++) {
			for (int x = 0; x < screenWidth; x++) {
				bi.setRGB(x, y, bgColor.getRGB());
			}
		}
	}

	// adds an operation to be applied to the image
	void addOperation(int startX, int startY, int endX, int endY, char type) {
		operations.add(new Operation(startX, startY, endX, endY, type));
	}

	BufferedImage getBufferedImage() {
		return bi;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();

		while (MouseArt.state != mouseart.State.STOPPED) {
			if (System.currentTimeMillis() - start > 50) { // Update preview in UI thread
				start = System.currentTimeMillis();
				imageView.setImage(resize());
			}
			if (MouseArt.state == mouseart.State.RECORDING) {
				while (operations.size() > 0) {
					Operation o = operations.pop();
					if (o.getType() == 'l') drawLine(o);
					if (o.getType() == 'c') drawCircle(o);
				}
			} else {
				try {
					sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// creates an image that can be used by JavaFX and resizes to fit the view

	/**
	 * @return
	 */
	private Image resize() {
		java.awt.Image image = bi.getScaledInstance(MouseArt.SCENE_WIDTH, MouseArt.SCENE_HEIGHT, java.awt.Image.SCALE_SMOOTH);
		BufferedImage bi = new BufferedImage(MouseArt.SCENE_WIDTH, MouseArt.SCENE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return SwingFXUtils.toFXImage(bi, null);
	}

	// draws a straight line between coordinates in operation object
	private void drawLine(Operation o) {

		// determines biggest step that can be taken without skipping any pixels
		float divisor = 1;
		if (Math.abs(o.getEndX() - o.getStartX()) > Math.abs(o.getEndY() - o.getStartY())) {
			for (; Math.abs(o.getEndX() - o.getStartX()) / divisor > 1.0f; divisor++) ;
		} else {
			for (; Math.abs(o.getEndY() - o.getStartY()) / divisor > 1.0f; divisor++) ;
		}

		// white... temporary
		Color c = new Color(0, 0, 0);

		// draws line in image
		float x = o.getStartX(), y = o.getStartY();
		while ((o.getStartX() > o.getEndX() && x >= o.getEndX()) || (o.getEndX() > o.getStartX() && x <= o.getEndX()) || (o.getStartY() > o.getEndY() && y >= o.getEndY()) || (o.getEndY() > o.getStartY() && y <= o.getEndY())) {
			bi.setRGB((int) x, (int) y, c.getRGB());

			x += (o.getEndX() - o.getStartX()) / divisor;
			y += (o.getEndY() - o.getStartY()) / divisor;
		}
	}

	// draws a circle along within specified coordinates with another circle inside which is filled
	private void drawCircle(Operation o) {
		int radius = (o.getEndX() - o.getStartX()) / 2;
		int center = (o.getEndX() + o.getStartX()) / 2;

		Color c = new Color(100, 100, 100);

		// creates outer circle
		for (float a = 0; a < 2 * Math.PI; a += Math.PI / 1000) {
			int x = (int) (center + (Math.cos(a) * radius));
			int y = (int) (o.getStartY() + (Math.sin(a) * radius));
			if (x > 0 && x < MouseArt.screenWidth && y > 0 && y < MouseArt.screenHeight) {
				bi.setRGB((int) (center + (Math.cos(a) * radius)), (int) (o.getStartY() + (Math.sin(a) * radius)),
						c.getRGB());
			}
		}

		radius /= 10; // radius of inner circle

		// draw inner circle
		for (float x = 0; x < 2 * Math.PI; x += Math.PI / 100) {
			int a = (int) Math.round(center + (Math.cos(x) * radius));
			int b = (int) Math.round(o.getStartY() + (Math.sin(x) * radius));
			if (a < 0 || a > bi.getWidth() || b < 0 || b > bi.getHeight()) continue;

			bi.setRGB(a, b, c.getRGB());
		}

		// fill inner circle
		for (int y = o.getStartY() - radius; y < o.getStartY() + radius; y++) {
			int start = 0, end = 0;
			for (int a = center - radius; a < center; a++) {
				if (bi.getRGB(a, y) == c.getRGB()) {
					start = a;
					for (int b = center; b < center + radius; b++) {
						if (bi.getRGB(b, y) == c.getRGB()) {
							end = b;
							break;
						}
					}
					break;
				}
			}

			for (int x = start; x < end; x++)
				bi.setRGB(x, y, c.getRGB());
		}
	}
}

class Operation {

    /*
		Records starting and ending coordinates for a specific type of operation to be applied to the image
     */

	private int startX, startY, endX, endY;
	private char type; // type of operation to be executed

	Operation(int startX, int startY, int endX, int endY, char type) {
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.type = type;
	}

	char getType() {
		return type;
	}

	int getStartX() {
		return startX;
	}

	int getStartY() {
		return startY;
	}

	int getEndX() {
		return endX;
	}

	int getEndY() {
		return endY;
	}
}
