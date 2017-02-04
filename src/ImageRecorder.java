/*
    Records all mouse movement and adds operations for the ImageHandler thread to process
 */

import java.awt.*;

public class ImageRecorder extends Thread{

    private Point prevLocation = MouseInfo.getPointerInfo().getLocation();
    private ImageHandler im;

    ImageRecorder(ImageHandler im) {
        this.im = im;
    }

    @Override
    public void run() {

        float m = MouseArt.canvasSizeMultiplier;
        long start = System.currentTimeMillis();
        long diff;

        while(true) {

            Point location = MouseInfo.getPointerInfo().getLocation();

            // stop recording when recording state equals stopped
            if (MouseArt.state == 's')
                break;
            // when mouse is moving add linear operation
            if (MouseArt.state == 'r' && !prevLocation.equals(location)) {
                if ((diff = System.currentTimeMillis() - start) > 3000) {
                    im.addOperation((int) (prevLocation.getX() - Math.cbrt(diff)), (int) prevLocation.getY(), (int) (prevLocation.getX() + Math.cbrt(diff)), (int) prevLocation.getY(), 'c');
                }
                start = System.currentTimeMillis();
                im.addOperation((int)(location.x * m), (int)(location.y * m), (int)(prevLocation.x * m), (int)(prevLocation.y * m), 'l');
            }
            prevLocation = MouseInfo.getPointerInfo().getLocation();
            synchronized (this) {
                try {
                    Thread.sleep(16);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
