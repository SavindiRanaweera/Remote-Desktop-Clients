package lk.ijse.dep13.remote.shared.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
}
