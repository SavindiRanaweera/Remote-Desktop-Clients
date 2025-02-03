package lk.ijse.dep13.remote.client.controller;

import com.github.sarxos.webcam.Webcam;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import lk.ijse.dep13.remote.shared.util.StreamHandler;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ActiveSceneController {
    public ImageView imgAudio;
    public  ImageView imgEndCall;
    public  ImageView imgMicOff;
    public  ImageView imgMicOn;
    public  ImageView imgVideo;
    public  ImageView imgVideoOff;
    public  ImageView imgVideoOn;
    public  AnchorPane root;


    private AudioFormat format;

    public void initialize() {
        imgVideoOff.setVisible(true);
        imgVideoOn.setVisible(false);
        imgMicOff.setVisible(true);
        imgMicOn.setVisible(false);
    }

    public void startClient() throws IOException {
        Webcam webcam = Webcam.getDefault();
        webcam.open();

        Socket videoSendSocket = new Socket("127.0.0.1", 9090);
        Socket audioSendSocket = new Socket("127.0.0.1", 9091);

//        ServerSocket videoReceiveSocket = new ServerSocket(9092);
//        ServerSocket audioReceiveSocket = new ServerSocket(9093);

        System.out.println("Client started!");

        new Thread(() -> {sendVideo ( webcam, videoSendSocket );}).start ();
        new Thread(() -> {sendAudio ( audioSendSocket );}).start ();
        new Thread(() -> {receiveVideo ( videoSendSocket );}).start ();
        new Thread(() -> {receiveAudio ( audioSendSocket );}).start ();
    }

    private void sendVideo(Webcam webcam, Socket socket) {
        try {
           while (true) {
               BufferedImage image = webcam.getImage ( );
               StreamHandler.sendVideo ( socket, image );
               Thread.sleep ( 1000 / 27 );
           }
        } catch (InterruptedException e) {
               e.printStackTrace ();
        }
    }

    private void receiveVideo(Socket socket) {
        try  {
            BufferedImage image = StreamHandler.receiveVideo ( socket );
            Platform.runLater( () -> {imgVideo.setImage ( new Image ( String.valueOf ( image ) ) );});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAudio(Socket socket) {
        try {
            format = new AudioFormat(44100, 16, 2, true, false);
            TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(new DataLine.Info(TargetDataLine.class, format));
            microphone.open(format);
            microphone.start();
            StreamHandler.sendAudio ( socket, microphone );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveAudio(Socket socket) {
        try {
            format = new AudioFormat(44100, 16, 2, true, false);
            SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
            speakers.open(format);
            speakers.start();
            StreamHandler.receiveAudio ( socket, speakers );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void imgOnMouseClicked( MouseEvent event) throws IOException {
        if (imgVideoOff.isVisible()) {
            imgVideoOff.setVisible(false);
            imgVideoOn.setVisible(true);
            startClient();
        } else if (imgVideoOn.isVisible()) {
            imgVideoOn.setVisible(false);
            imgVideoOff.setVisible(true);
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
        ScaleTransition ft = new ScaleTransition( Duration.millis(200), (ImageView) event.getTarget());
        ft.setFromX(1.1);
        ft.setFromY(1.1);
        ft.setToX(1);
        ft.setToY(1);
        ft.playFromStart();
    }
}
