package lk.ijse.dep13.remote.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SeverAppInitializer {
    private static final int PORT = 9090;
    private static String sharedEditorContent = "";
    public static void main(String[] args)  {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException( e );
                    } catch (InterruptedException e) {
                        throw new RuntimeException( e );
                    }
                } ).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException, InterruptedException {
        try (Socket socket = clientSocket) {
            changeDesktopColor( "Red" );
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            InputStream is = socket.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ObjectInputStream ois = new ObjectInputStream(bis);

            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

            // Start a thread to capture and send the screen
            new Thread(() -> {
                try {
                    while (true) {
                        BufferedImage screenCapture = robot.createScreenCapture(screenRect);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ImageIO.write(screenCapture, "jpg", baos);
                        byte[] imageBytes = baos.toByteArray();
                        oos.writeObject(imageBytes);
                        oos.flush();

                        Thread.sleep(1000/27);
                    }
                } catch (Exception e) {
                    System.out.println("Screen capture stopped: " + e.getMessage());
                }
            }).start();

            // Handle incoming messages and update editor content
            while (true) {
                Object received = ois.readObject();
                if (received instanceof String) {
                    String message = (String) received;

                    if (message.startsWith("MOUSE:")) {
                        String[] coords = message.substring(6).split(",");
                        int x = Integer.parseInt(coords[0]);
                        int y = Integer.parseInt(coords[1]);
                        robot.mouseMove(x, y);
                    } else if (message.startsWith("KEY:")) {
                        char keyChar = message.substring(4).charAt(0);
                        int keyCode = KeyEvent.getExtendedKeyCodeForChar(keyChar);
                        robot.keyPress(keyCode);
                        robot.keyRelease(keyCode);
                    } else if (message.startsWith("EDITOR:")) {
                        sharedEditorContent = message.substring(7);
                        System.out.println("Updated editor content: " + sharedEditorContent);
                        oos.writeObject("EDITOR:" + sharedEditorContent);
                        oos.flush();
                    }
                }
            }
        } catch (Exception e) {
            revertDesktop();
            System.out.println("Client disconnected: " + e.getMessage());
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
