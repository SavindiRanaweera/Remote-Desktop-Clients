package lk.ijse.dep13.remote.client.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AppRouter {
    public enum Routes {
    DIAL, APPROVAL, ACTIVE
    }

    public static AnchorPane getContainer( Routes route) throws IOException {
        AnchorPane container = null;
        if(route == Routes.APPROVAL){
            container = FXMLLoader.load(AppRouter.class.getResource( "/scene/ApprovalScene.fxml" ));
        }else if(route == Routes.DIAL){
            container = FXMLLoader.load(AppRouter.class.getResource( "/scene/DialScene.fxml" ));
        }else if(route == Routes.ACTIVE){
            container = FXMLLoader.load(AppRouter.class.getResource( "/scene/ActiveScene.fxml" ));
        }

        return container;
    }
}
