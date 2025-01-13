package lk.ijse.dep13.remote.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.dep13.remote.server.controller.FileReceiverSceneController;
import lk.ijse.dep13.remote.server.util.AppRouter;

import java.io.*;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAppInitializer extends Application {

    public static Scene scene;
    static ServerSocket serverSocket;
    Stage stage;
    public static String path="/home/yashoda/Documents/dep-13/first-phase/git-demos/Remote-Desktop-Clients/client-app/Yashoda";
    @Override
    public void start ( Stage primaryStage ) throws IOException {
        scene = new Scene( AppRouter.getContainer( AppRouter.Routes.MAIN));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Remote Desktop Client");
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.centerOnScreen();
        stage = primaryStage;
        // Start the server thread
        try {
            new Thread(ServerAppInitializer::startServer).start();
        } catch (Exception e) {
            throw new RuntimeException( e );
        }

    }

    public static void main(String[] args) throws IOException {
        launch();
//        try {
//            serverSocket = new ServerSocket(6060);
//            System.out.println("Server started on port 6060");
//
//        } catch (BindException e) {
//            System.out.println("6060 port is already in use");
//            serverSocket = new ServerSocket(0);
//            System.out.println("Re-try: server started on port " + serverSocket.getLocalPort());
//        }

        while (true){
            System.out.println("Waiting for connection");
            Socket localSocket = serverSocket.accept();
            System.out.println("Accepted connection from" + localSocket.getRemoteSocketAddress());
            handleUI(localSocket);
        }
    }

    public static void startServer() {
        try {
            serverSocket = new ServerSocket(6060);
            System.out.println("Server started on port 6060");
            while (true){
                System.out.println("Waiting for connection");
                Socket localSocket = serverSocket.accept();
                System.out.println("Accepted connection from" + localSocket.getRemoteSocketAddress());
                handleUI(localSocket);
            }
        } catch (IOException e) {
            System.out.println("6060 port is already in use");
//            serverSocket = new ServerSocket(0);
//            System.out.println("Re-try: server started on port " + serverSocket.getLocalPort());
        }
    }

    public static void handleUI(Socket localSocket) {
        new Thread(()->{
            try {
                showReceiveView();
                System.out.println("Server checking");
                InputStream is = localSocket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String username = br.readLine().strip();
                System.out.println("?????"+username);
                if (!(username.length() >= 3 && username.strip().length() <= 10)){
                    System.out.println("Invalid username");
                    return;
                }

                File userFolder = new File(path, username);
                userFolder.mkdir();

                String fileName = br.readLine().strip();
                System.out.println("FName"+fileName);
                if (fileName.isEmpty()){
                    System.out.println("Invalid filename");
                    return;
                }

                File file = new File(userFolder, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                BufferedInputStream bis = new BufferedInputStream(is);

                while (true){
                    byte[] buffer = new byte[1024];
                    int read = bis.read(buffer);
                    if (read == -1){
                        break;
                    }
                    bos.write(buffer, 0, read);
                }
                System.out.printf("File: %s uploaded successfully%n", fileName);
                bos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private static void showReceiveView() throws IOException {




        Platform.runLater(() -> {
            //FileReceiverSceneController controller = (FileReceiverSceneController) AppRouter.getController(AppRouter.Routes.MAIN);
            //controller.show();
            try {
                openPopup();
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        });
    }

    private static void openPopup() throws IOException {
        Stage  stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader( ServerAppInitializer.class.getResource("/scene/FileReceiverScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //((MediaPlayController) fxmlLoader.getController()).initData(file);
        stage.setScene(scene);
        stage.setTitle("File Received");
        stage.setResizable(true);
        stage.show();
    }
}