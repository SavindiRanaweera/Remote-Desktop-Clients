package lk.ijse.dep13.remote.server;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.dep13.remote.server.util.AppRouter;

import java.io.IOException;

public class ServerAppInitializer extends Application {

    public static void main ( String[] args ) {
        launch ( args );
    }

    @Override
    public void start ( Stage primaryStage ) throws IOException {
        primaryStage.setScene(new Scene ( AppRouter.getContainer(AppRouter.Routes.DIAL)));
        primaryStage.setTitle("Desktop Server");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}
