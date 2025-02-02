package lk.ijse.dep13.remote.shared.util;

import javax.imageio.ImageIO;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.Buffer;

public class StreamHandler {
    public static void sendVideo( Socket socket, BufferedImage image) {
        try {
            OutputStream os = socket.getOutputStream ( );
            ObjectOutputStream oos = new ObjectOutputStream ( os );
            ByteArrayOutputStream baos = new ByteArrayOutputStream ( );
            ImageIO.write(image, "jpg", baos);
            oos.writeObject( baos.toByteArray ( ) );
            oos.flush ( );
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public static BufferedImage receiveVideo( Socket socket) {
        try {
            InputStream is = socket.getInputStream ( );
            ObjectInputStream ois = new ObjectInputStream ( is );
            byte[] imageByte = (byte[])ois.readObject ( );
            ByteArrayInputStream bais = new ByteArrayInputStream ( imageByte );
            BufferedImage image = ImageIO.read ( bais );
            return image;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException ( e );
        }
    }

    public static void sendAudio( Socket socket, TargetDataLine mic ) {
        try {
            byte[] buffer = new byte[ 4096 ];
            OutputStream os = socket.getOutputStream ( );
            while (true){
                int bytesRead = mic.read(buffer, 0, buffer.length);
                os.write(buffer, 0, bytesRead);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }
    }

    public static void receiveAudio( Socket socket, SourceDataLine speaker) {
        try {
            InputStream is = socket.getInputStream ( );
            byte[] buffer = new byte[ 4096 ];
            while (true){
                int bytesRead = is.read(buffer);
                if (bytesRead > 0) {
                    speaker.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace ();
        }

    }
}
