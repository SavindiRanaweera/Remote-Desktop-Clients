package lk.ijse.dep13.remote.server.controller;

import com.github.sarxos.webcam.Webcam;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep13.remote.server.util.AppRouter;

import java.io.*;

public class ApprovalSceneController {
    public ImageView imgAccept;
    public  ImageView imgReject;
    public  ImageView imgVideoTransition;
    public  AnchorPane root;

    public void initialize() {
        TranslateTransition verticalTransition = new TranslateTransition ();
        verticalTransition.setNode(imgVideoTransition);
        verticalTransition.setDuration(Duration.seconds(1));
        verticalTransition.setByY(100);
        verticalTransition.setAutoReverse(true);
        verticalTransition.setCycleCount(TranslateTransition.INDEFINITE);
        verticalTransition.setFromY(-50);
        verticalTransition.setToY(50);
        verticalTransition.play();
    }

    public void imgOnMouseClicked(MouseEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ImageView imageView = ( ImageView ) event.getTarget ( );
        if(imageView == imgAccept ) {
            stage.setScene ( new Scene ( AppRouter.getContainer ( AppRouter.Routes.ACTIVE ) ) );
        }else if(imageView == imgReject ) {
            ((Stage)(root.getScene().getWindow())).close();
        }
    }

    public void imgOnMouseEntered(MouseEvent event) {
        ScaleTransition ft = new ScaleTransition( Duration.millis(200), (ImageView) event.getTarget());
        ft.setFromX(1);
        ft.setFromY(1);
        ft.setToX(1.1);
        ft.setToY(1.1);
        ft.playFromStart();
    }

    public void imgOnMouseExited(MouseEvent event) {
        ScaleTransition ft = new ScaleTransition(Duration.millis(200), (ImageView) event.getTarget());
        ft.setFromX(1.1);
        ft.setFromY(1.1);
        ft.setToX(1);
        ft.setToY(1);
        ft.playFromStart();
    }
}

