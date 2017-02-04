/*
    Applies all operations submitted by the ImageRecorder thread o the operations ArrayDeque
    Owns the "canvas" image where the drawing occurs
 */

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

public class ImageHandler extends Thread {

    private BufferedImage bi;
    private ArrayDeque<Operation> operations;

    private ImageView imageView;

    ImageHandler(int screenWidth, int screenHeight, ImageView imageView) {
        this.imageView = imageView;

        bi = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_RGB);

        // fills image with white color
        for (int y = 0; y < screenHeight; y++) {
            for (int x = 0; x < screenWidth; x++) {
                bi.setRGB(x, y, new Color(255, 255, 255).getRGB());
            }
        }

        operations = new ArrayDeque<>();
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

        while (MouseArt.state == 'r' || MouseArt.state == 'p') {
            // updates image displayed in UI thread
            if (System.currentTimeMillis() - start > 33) {
                start = System.currentTimeMillis();
                imageView.setImage(resize());
            }
            // applies operations to image if any exist
            if (MouseArt.state == 'r' && operations.size() > 0) {
                Operation o = operations.pop();
                if (o.getType() == 'l')
                    drawLine(o);
                if (o.getType() == 'c')
                    drawCircle(o);
            }
            else {
                // stop recording if state in main thread changes to 's' or sleep this thread if no operations available
                if (MouseArt.state == 's' && operations.size() == 0)
                    break;
                synchronized (this) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // creates an image that can be used by JavaFX and resizes to fit the view
    private Image resize() {
        java.awt.Image image = bi.getScaledInstance(MouseArt.SCENEWIDTH, MouseArt.SCENEHEIGHT, java.awt.Image.SCALE_SMOOTH);
        BufferedImage bi = new BufferedImage(MouseArt.SCENEWIDTH, MouseArt.SCENEHEIGHT, BufferedImage.TYPE_INT_RGB);

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
            for (; Math.abs(o.getEndX() - o.getStartX()) / divisor > 1.0f; divisor++);
        } else {
            for (; Math.abs(o.getEndY() - o.getStartY()) / divisor > 1.0f; divisor++);
        }

        // white... temporary
        Color c = new Color(0, 0, 0);

        // draws line in image
        float x = o.getStartX(), y = o.getStartY();
        while ((o.getStartX() > o.getEndX() && x >= o.getEndX()) || (o.getEndX() > o.getStartX() && x <= o.getEndX()) ||
                (o.getStartY() > o.getEndY() && y >= o.getEndY()) || (o.getEndY() > o.getStartY() && y <= o.getEndY())) {
            bi.setRGB((int) x, (int) y, c.getRGB());

            x += (o.getEndX() - o.getStartX()) / divisor;
            y += (o.getEndY() - o.getStartY()) / divisor;
        }
    }

    // draws a circle along within specified coordinates with another circle inside which is filled
    private void drawCircle(Operation o) {
        int radius = (o.getEndX() - o.getStartX()) / 2;
        int center = (o.getEndX() + o.getStartX()) / 2;

        Color c = new Color(0, 0, 0);

        // creates outer circle
        for (float x = 0; x < 2 * Math.PI; x += Math.PI / 1000) {
            bi.setRGB((int)(center + (Math.cos(x) * radius)), (int)(o.getStartY() + (Math.sin(x) * radius)), c.getRGB());
        }

        radius /= 10; // radius of inner circle

        // draw inner circle
        for (float x = 0; x < 2 * Math.PI; x += Math.PI / 100) {
            int a = (int)(center + (Math.cos(x) * radius));
            int b = (int)(o.getStartY() + (Math.sin(x) * radius));
            if (a < 0 || a > bi.getWidth() || b < 0 || b > bi.getHeight())
                continue;

            bi.setRGB(a, b, c.getRGB());
        }

        // fill inner circle
        for (int y = o.getStartY() - radius; y < o.getStartY() + radius; y++) {
            int start = 0, end = 0;
            for (int a = center - radius; a < center + radius; a++) {
                if (bi.getRGB(a, y) == c.getRGB()) {
                    start = a;
                    for (int b = a + 1; b < center + radius; b++) {
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
