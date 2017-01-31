import java.awt.*;

public class ImageRecorder extends Thread{

    private boolean recording = true;
    private Point prevLocation = MouseInfo.getPointerInfo().getLocation();
    private ImageHandler im;

    ImageRecorder(ImageHandler im) {
        this.im = im;
    }

    @Override
    public void run() {
        while(true) {

            Point location = MouseInfo.getPointerInfo().getLocation();

            if (!recording)
                break;
            if (!prevLocation.equals(location)) {
                //System.out.println(location);
                im.addOperation(location.x, location.y, prevLocation.x, prevLocation.y, 'l');
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

    public void setRecording(boolean recording) {
        this.recording = recording;
    }
}
