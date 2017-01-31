import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MouseArt extends Application{

    private ImageHandler im;
    private Thread IMThread;
    private ImageRecorder ir;
    private Thread IRThread;
    private boolean recording = false;

    private Scene scene;
    private Pane pane;
    private Button startStop;

    private int screenWidth, screenHeight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        screenWidth = (int) Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX();
        screenHeight = (int)Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY();

        pane = new Pane();
        scene = new Scene(pane, 800, 600);

        startStop = new Button("Start");
        startStop.relocate(400, 300);
        pane.getChildren().addAll(startStop);

        startStop.setOnMouseClicked(event -> {
            if (im == null) {
                startStop.setText("Stop");
                startRecording();
            } else
                stopRecording();
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startRecording() {

        im = new ImageHandler(screenWidth, screenHeight);
        IMThread = new Thread(im);
        IMThread.setDaemon(true);
        IMThread.start();

        ir = new ImageRecorder(im);
        IRThread = new Thread(ir);
        IRThread.setDaemon(true);
        IRThread.start();
    }

    private void stopRecording() {
        im.setRunning(false);
        ir.setRecording(false);

        try {
            IMThread.join();
            IRThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ImageIO.write(im.getBufferedImage(), "PNG", new File("test.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ir = null;
        im = null;
        IRThread = null;
        IMThread = null;

        System.out.println("Done");

        startStop.setText("Start");
    }
}
