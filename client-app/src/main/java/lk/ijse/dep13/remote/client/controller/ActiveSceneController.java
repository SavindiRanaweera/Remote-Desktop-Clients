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

        Socket videoSocket = new Socket("127.0.0.1", 9092); // Sending video to the server
        Socket audioSocket = new Socket("127.0.0.1", 9093); // Sending audio to the server

        new Thread(() -> sendVideo(webcam, videoSocket)).start();
        new Thread(() -> sendAudio(audioSocket)).start();

        Socket videoReceiveSocket = new Socket("127.0.0.1", 9090); // Receiving video from server
        Socket audioReceiveSocket = new Socket("127.0.0.1", 9091); // Receiving audio from server

        new Thread(() -> receiveVideo(videoReceiveSocket)).start();
        new Thread(() -> receiveAudio(audioReceiveSocket)).start();
    }

    private void sendVideo(Webcam webcam, Socket socket) {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {
            while (true) {
                BufferedImage image = webcam.getImage();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", baos);
                oos.writeObject(baos.toByteArray());
                oos.flush();
                Thread.sleep(1000 / 30);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void receiveVideo(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                byte[] imageBytes = (byte[]) ois.readObject();
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                Platform.runLater(() -> imgVideo.setImage(image));
            }
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

            OutputStream os = socket.getOutputStream();
            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
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

            InputStream is = socket.getInputStream();
            byte[] buffer = new byte[4096];

            while (true) {
                int bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    speakers.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void imgOnMouseClicked( MouseEvent event) throws IOException {
        ImageView imageView = (ImageView) event.getTarget();
        if (imageView == imgVideoOff) {
            imgVideoOff.setVisible(false);
            imgVideoOn.setVisible(true);
            startClient();
        } else if (imageView == imgVideoOn) {
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
