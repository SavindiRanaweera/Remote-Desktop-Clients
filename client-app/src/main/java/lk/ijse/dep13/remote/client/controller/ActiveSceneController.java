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

        Socket videoSendSocket = new Socket("127.0.0.1", 9090);
        Socket audioSendSocket = new Socket("127.0.0.1", 9091);

        ServerSocket videoReceiveSocket = new ServerSocket(9092);
        ServerSocket audioReceiveSocket = new ServerSocket(9093);

        System.out.println("Client started!");

        new Thread(() -> {
            try {
                receiveVideo(videoReceiveSocket);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException ( e );
            }
        } ).start();
        new Thread(() -> sendVideo(webcam, videoSendSocket)).start();
        new Thread(() -> {
            try {
                receiveAudio(audioReceiveSocket);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException ( e );
            }
        } ).start();
        new Thread(() -> sendAudio(audioSendSocket)).start();
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

    private void receiveVideo(ServerSocket serverSocket) throws ClassNotFoundException {
        try{
            Socket socket = serverSocket.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            while (true) {
                byte[] imageBytes = (byte[]) ois.readObject();
                Image image = new Image(new ByteArrayInputStream(imageBytes));
                Platform.runLater(() -> imgVideo.setImage(image));
            }
        } catch (IOException e) {
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

    private void receiveAudio(ServerSocket serverSocket) throws ClassNotFoundException {
        try {
            Socket socket = serverSocket.accept ();
            InputStream is = socket.getInputStream();
            format = new AudioFormat(44100, 16, 2, true, false);
            SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
            speakers.open(format);
            speakers.start();
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
