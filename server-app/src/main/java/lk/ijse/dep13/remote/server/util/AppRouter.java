package lk.ijse.dep13.remote.server.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AppRouter {
    public enum Routes {
        MAIN
    }

    public static AnchorPane getContainer( Routes route) throws IOException {
        AnchorPane container = null;
        if(route == Routes.MAIN){
            container = FXMLLoader.load(AppRouter.class.getResource("/scene/MainScene.fxml"));
        }

        return container;
    }
}
