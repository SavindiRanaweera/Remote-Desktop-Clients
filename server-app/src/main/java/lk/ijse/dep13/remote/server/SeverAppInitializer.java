package lk.ijse.dep13.remote.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SeverAppInitializer {
    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println("Server started on port 9090");
        while (true) {
            System.out.println("Waiting for connection...");
            Socket localSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + localSocket.getRemoteSocketAddress());
            new Thread(()->{
                try {
                    changeDesktopColor( "red" );
                    OutputStream os = localSocket.getOutputStream();
                    BufferedOutputStream bos = new BufferedOutputStream(os);
                    ObjectOutputStream oos = new ObjectOutputStream(bos);

                    while (true) {
                        Robot robot = new Robot();
                        BufferedImage screen = robot
                                .createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(screen, "jpeg", baos);
                        oos.writeObject(baos.toByteArray());
                        oos.flush();
                        Thread.sleep(1000 / 27);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    try {
                        revertDesktop();
                    } catch (IOException e) {
                        throw new RuntimeException( e );
                    } catch (InterruptedException e) {
                        throw new RuntimeException( e );
                    }
                }
            }).start();
            new Thread(()->{
                try {
                    InputStream is = localSocket.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    ObjectInputStream ois = new ObjectInputStream(bis);

                    Robot robot = new Robot();
                    while (true){
                        Point coordinates = (Point) ois.readObject();
                        robot.mouseMove(coordinates.x, coordinates.y);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
    public static void changeDesktopColor(String color) throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            // Set picture options to 'none' (no wallpaper image)
            String[] command1 = {"gsettings", "set", "org.gnome.desktop.background", "picture-options", "none"};
            Runtime.getRuntime().exec(command1).waitFor();

            // Set the primary color for the desktop background (ensure it's in hex format)
            String[] command2 = {"gsettings", "set", "org.gnome.desktop.background", "primary-color", color.startsWith("#") ? color : "#" + color};
            Runtime.getRuntime().exec(command2).waitFor();

            System.out.println("Desktop color changed to: " + color);
        } else {
            System.err.println("Changing desktop color is only supported on Linux with GNOME.");
        }
    }

    public static void revertDesktop() throws IOException, InterruptedException {
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            // Reset wallpaper picture options to 'zoom' (default behavior)
            String[] revertCommand = {"gsettings", "set", "org.gnome.desktop.background", "picture-options", "zoom"};
            Runtime.getRuntime().exec(revertCommand).waitFor();

            System.out.println("Desktop settings reverted to default.");
        } else {
            System.err.println("Reverting desktop color is only supported on Linux with GNOME.");
        }
    }

}
