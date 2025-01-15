package lk.ijse.dep13.remote.client.controller;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MainSceneController {
    public ImageView imgScreen;
    public AnchorPane root;
    private Socket socket;

    public void initialize() throws Exception {

        imgScreen.fitWidthProperty().bind( root.widthProperty() );
        imgScreen.fitHeightProperty().bind( root.heightProperty() );

        socket = new Socket( "127.0.0.1", 9090 );
        OutputStream os = socket.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream( os );
        ObjectOutputStream oos = new ObjectOutputStream( bos );

        imgScreen.setOnMouseMoved( mouseEvent -> {
            try {
                // Send mouse move coordinates
                Point coordinates = new Point( (int) mouseEvent.getX(), (int) mouseEvent.getY() );
                Dimension imgDimensions = new Dimension( (int) imgScreen.getBoundsInParent().getWidth(),
                        (int) imgScreen.getBoundsInParent().getHeight() );
                Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

                // Send mouse move event
                oos.writeObject( new Object[]{"MOVE", coordinates, imgDimensions, screenDimensions} );
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } );

// Add mouse click listener
        imgScreen.setOnMouseClicked( mouseEvent -> {
            try {
                String button = switch ( mouseEvent.getButton() ) {
                    case PRIMARY -> "LEFT";    // Left click
                    case SECONDARY -> "RIGHT"; // Right click
                    case MIDDLE -> "MIDDLE";   // Middle click
                    default -> "UNKNOWN";
                };

                // Send mouse click event
                oos.writeObject( new Object[]{"CLICK", button} );
                oos.flush();
                System.out.println( "Mouse click event sent: "+button );
            } catch (Exception e) {
                e.printStackTrace();
            }
        } );

        Task<javafx.scene.image.Image> task = new Task<>() {
            @Override
            protected javafx.scene.image.Image call() throws Exception {
                InputStream is = socket.getInputStream();
                BufferedInputStream bis = new BufferedInputStream( is );
                ObjectInputStream ois = new ObjectInputStream( bis );
                while (true) {
                    byte[] image = (byte[]) ois.readObject();
                    ByteArrayInputStream bais = new ByteArrayInputStream( image );
                    javafx.scene.image.Image screen = new Image( bais );
                    updateValue( screen );
                }
            }
        };

        imgScreen.imageProperty().bind( task.valueProperty() );
        new Thread( task ).start();
    }


}
