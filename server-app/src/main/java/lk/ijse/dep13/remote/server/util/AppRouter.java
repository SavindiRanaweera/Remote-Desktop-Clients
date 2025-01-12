package lk.ijse.dep13.remote.server.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AppRouter {
    public enum Routes {
        DIAL, ACTIVE, APPROVAL
    }

    public static AnchorPane getContainer( Routes route) throws IOException {
        AnchorPane container = null;
        if(route == Routes.DIAL) {
            container = FXMLLoader.load ( AppRouter.class.getResource ( "/scene/DialScene.fxml" ) );
        }else if(route == Routes.ACTIVE) {
            container = FXMLLoader.load ( AppRouter.class.getResource ( "/scene/Active.fxml" ) );
        }else if(route == Routes.APPROVAL) {
            container =  FXMLLoader.load ( AppRouter.class.getResource ( "/scene/Approval.fxml" ) );
        }

        return container;
    }
}
