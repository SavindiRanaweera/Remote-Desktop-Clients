package lk.ijse.dep13.remote.shared.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

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


}
