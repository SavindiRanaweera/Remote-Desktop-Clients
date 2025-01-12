package lk.ijse.dep13.remote.client.controller;


import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep13.remote.client.util.AppRouter;

import java.io.IOException;

public class DialSceneController {
    //public ImageView imgDial;
    public ImageView imgVideo;
    public AnchorPane root;

    public void imgOnMouseClicked(MouseEvent event) throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        ImageView imageView = (ImageView) event.getTarget();
        if(imageView == imgVideo){
            stage.setScene ( new Scene ( AppRouter.getContainer ( AppRouter.Routes.ACTIVE ) ) );
        }
        stage.sizeToScene();
        stage.centerOnScreen();

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
