import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;

public class ImageHandler extends Thread {

    private boolean running;

    private BufferedImage bi;
    private ArrayDeque<Operation> operations;

    ImageHandler(int screenWidth, int screenHeight) {
        bi = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < screenHeight; y++) {
            for (int x = 0; x < screenWidth; x++) {
                bi.setRGB(x, y, new Color(255, 255, 255).getRGB());
            }
        }

        operations = new ArrayDeque<>();
    }

    void setRunning(boolean running) {
        this.running = running;
    }

    void addOperation(int startX, int startY, int endX, int endY, char type) {
        operations.add(new Operation(startX, startY, endX, endY, type));
    }

    BufferedImage getBufferedImage() {
        return bi;
    }

    @Override
    public void run() {
        running = true;

        while (true) {
            if (operations.size() > 0) {
                Operation o = operations.pop();
                if (o.getType() == 'l')
                    linearMatch(o);
            }
            else {
                if (!running && operations.size() == 0)
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

    private void linearMatch(Operation o) {

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
}

class Operation {

    private int startX, startY, endX, endY;
    private char type;

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
