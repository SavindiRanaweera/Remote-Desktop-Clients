package lk.ijse.dep13.remote.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep13.remote.client.util.AppRouter;

import java.io.IOException;

public class ClientAppInitializer extends Application {

    public static void main ( String[] args ) {
        launch ( args );
    }

    @Override
    public void start ( Stage primaryStage ) throws IOException {
        primaryStage.setScene(new Scene ( AppRouter.getContainer(AppRouter.Routes.MAIN)));
        primaryStage.setTitle("Client Text Message");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
