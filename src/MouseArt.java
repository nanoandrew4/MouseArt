/*
    Main UI thread
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MouseArt extends Application{

    static int SCENEWIDTH;
    static int SCENEHEIGHT;
    static char state; // r for recording, p for paused, s for stopped
    static float canvasSizeMultiplier = 1; // multiplier for the "canvas" or final image

    private ImageHandler im;
    private Thread IMThread;
    private ImageRecorder ir;
    private Thread IRThread;

    private Scene scene;
    private Pane pane;

    private int screenWidth, screenHeight;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        screenWidth = (int) (Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxX() * canvasSizeMultiplier);
        screenHeight = (int)(Screen.getScreens().get(Screen.getScreens().size() - 1).getBounds().getMaxY() * canvasSizeMultiplier);

        SCENEWIDTH = (int)(screenWidth * 0.25f);
        SCENEHEIGHT = (int)(screenHeight * 0.25f);

        pane = new Pane();
        scene = new Scene(pane, SCENEWIDTH, SCENEHEIGHT);

        // create menu items

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        Menu file = new Menu("File");

        MenuItem startRecording = new MenuItem("Start");
        startRecording.setOnAction(event -> {
            startRecording();
        });

        MenuItem pauseRecording = new MenuItem("Pause");
        pauseRecording.setOnAction(event -> {
            state = 'p';
        });

        MenuItem stopRecording = new MenuItem("Stop");
        stopRecording.setOnAction(event -> {
            stopRecording(primaryStage);
        });

        file.getItems().addAll(startRecording, pauseRecording, stopRecording);

        menuBar.getMenus().addAll(file);

        pane.getChildren().addAll(menuBar);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startRecording() {

        // starts the recording of mouse pointer and drawing the image

        state = 'r';

        ImageView imageView = new ImageView();

        im = new ImageHandler(screenWidth, screenHeight, imageView);
        IMThread = new Thread(im);
        IMThread.setDaemon(true);
        IMThread.start();

        ir = new ImageRecorder(im);
        IRThread = new Thread(ir);
        IRThread.setDaemon(true);
        IRThread.start();

        pane.getChildren().add(0, imageView);
    }

    private void stopRecording(Stage stage) {

        // stops the recording of the mouse pointer and saves created image

        state = 's';

        // wait for threads to finish before saving image
        try {
            IMThread.join();
            IRThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // save image
        save(stage);

        ir = null;
        im = null;
        IRThread = null;
        IMThread = null;

        pane = new Pane();
    }

    private void save(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        // set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        // show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        // save image
        if(file != null){
            try {
                ImageIO.write(im.getBufferedImage(), "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
