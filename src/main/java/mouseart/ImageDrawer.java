package mouseart;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mouseart.color_scheme.ColorScheme;
import mouseart.color_scheme.GrayScale;
import mouseart.geometry.Circle;
import mouseart.geometry.Geometry;
import mouseart.geometry.Line;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

/**
 * Draws all operations submitted by ImageRecorder to the BufferedImage and updates the UI preview periodically.
 */
public class ImageDrawer extends Thread {
	private BufferedImage bi;
	private ImageView imageView;

	ColorScheme colorScheme = new GrayScale();

	private ArrayDeque<Geometry> geometricDrawOps = new ArrayDeque<>();

	ImageDrawer(int screenWidth, int screenHeight, ImageView imageView) {
		this.setDaemon(true);
		this.setPriority(Thread.NORM_PRIORITY);
		this.imageView = imageView;

		bi = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

		// Fill in background with white (default is black)
		Color bgColor = new Color(255, 255, 255, 255);
		for (int y = 0; y < screenHeight; y++) {
			for (int x = 0; x < screenWidth; x++) {
				bi.setRGB(x, y, bgColor.getRGB());
			}
		}
	}

	/**
	 * Submits a job to draw a line. The line will be drawn soon after submission. This method takes a set of starting
	 * and ending coordinates, and adds a new instance of a Line object with these characteristics to the list of draw
	 * operations to be carried out.
	 *
	 * @param startX Line start coordinate on the x axis
	 * @param startY Line start coordinate on the y axis
	 * @param endX   Line end coordinate on the x axis
	 * @param endY   Line end coordinate on the y axis
	 */
	protected void addLineOp(int startX, int startY, int endX, int endY) {
		geometricDrawOps.add(new Line(startX, startY, endX, endY));
	}

	/**
	 * Submits a job to draw a circle. The operation will be carried out soon after
	 * submission. This method takes a centre coordinate and radius for the desired circle, and adds a new instance of
	 * a Circle object with these characteristics to the list of draw operations to be carried out.
	 *
	 * @param centreX Circle center on the x axis
	 * @param centreY Circle center on the y axis
	 * @param radius  Radius of the circle
	 */
	protected void addCircleOp(int centreX, int centreY, int radius, boolean fill, Integer... alpha) {
		geometricDrawOps.add(new Circle(centreX, centreY, radius, fill, alpha.length > 0 ? alpha[0] : 0));
	}

	BufferedImage getBufferedImage() {
		return bi;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();

		while (MouseArt.state != mouseart.State.STOPPED) {
			if (!MouseArt.stageMinimized && System.currentTimeMillis() - start > 50) { // Update preview in UI thread
				start = System.currentTimeMillis();
				imageView.setImage(convertToJFXImage());
			}
			if (MouseArt.state == mouseart.State.RECORDING) {
				while (geometricDrawOps.size() > 0) {
					Geometry g = geometricDrawOps.pop();
					if (g instanceof Line) drawLine((Line) g);
					if (g instanceof Circle) drawCircle((Circle) g);
				}
			} else {
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Converts the image being drawn on (BufferedImage) to an image that can be used by JavaFX to be displayed
	 * to the user.
	 *
	 * @return Image that can be inserted into ImageView for JavaFX
	 */
	private Image convertToJFXImage() {
		java.awt.Image image = bi.getScaledInstance(MouseArt.sceneWidth, MouseArt.sceneHeight,
				java.awt.Image.SCALE_SMOOTH);
		BufferedImage bi = new BufferedImage(MouseArt.sceneWidth, MouseArt.sceneHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g = bi.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return SwingFXUtils.toFXImage(bi, null);
	}

	/**
	 * Draws a straight line on the BufferedImage this class owns, submitted to the 'geometricDrawOps' list by the
	 * ImageRecorder class.
	 *
	 * @param l Line object, containing the starting and ending coordinates of the line
	 */
	private void drawLine(Line l) {

		/*
		 * Determines the step size to take on the coordinate plane to ensure that no pixels are skipped.
		 */
		float divisor = 1;
		if (Math.abs(l.endX - l.startX) > Math.abs(l.endY - l.startY))
			for (; Math.abs(l.endX - l.startX) / divisor > 1.0f; divisor++) ;
		else
			for (; Math.abs(l.endY - l.startY) / divisor > 1.0f; divisor++) ;

		Color lineColor = new Color(75, 75, 75);

		float x = l.startX, y = l.startY;
		// Draw line
		while ((l.startX > l.endX && x >= l.endX) || (l.endX > l.startX && x <= l.endX)
				|| (l.startY > l.endY && y >= l.endY) || (l.endY > l.startY && y <= l.endY)) {
			bi.setRGB((int) x, (int) y, lineColor.getRGB());

			x += (l.endX - l.startX) / divisor;
			y += (l.endY - l.startY) / divisor;
		}
	}

	/**
	 * Draws a circle on the BufferedImage this class owns with a specified radius, and another circle with a tenth
	 * of the radius, inside the larger circle. The inner circle is colored in, unlike the outer circle. The only color
	 * the outer circle receives is along its perimeter.
	 *
	 * @param c Circle object, containing centre coordinates and a radius
	 */
	private void drawCircle(Circle c) {
		float theta = 1.0f / c.radius;

		Color circleColor = colorScheme.getColor(c, c.alpha);

		for (float a = 0; a < 2 * Math.PI; a += theta) {
			int x = (int) (c.centerX - (Math.cos(a) * c.radius));
			int y = (int) (c.centerY - (Math.sin(a) * c.radius));
			if (x > 0 && x < MouseArt.screenWidth && y > 0 && y < MouseArt.screenHeight)
				bi.setRGB(x, y, circleColor.getRGB());

			if (c.fill && x < c.centerX) // Only fill from left side of the circle
				for (int l = 0; l < 2 * Math.abs(x - c.centerX); l++)
					if (x > 0 && x < MouseArt.screenWidth && y > 0 && y < MouseArt.screenHeight)
						bi.setRGB(x + l, y, circleColor.getRGB());
		}
	}
}