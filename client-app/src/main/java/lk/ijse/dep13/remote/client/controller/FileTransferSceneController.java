package lk.ijse.dep13.remote.client.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.Socket;

public class FileTransferSceneController {
    public AnchorPane root;
    public Button btnBrowse;
    public Button btnSend;
    public TextField txtBrowse;
    public Label lblFileTransfer;
    public Button btnCancel;

    public void btnBrowseOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter( ".png","*.*"));

        File file = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (file != null) {
            txtBrowse.setText(file.getAbsolutePath());
        }


    }

    public void btnSendOnAction(ActionEvent actionEvent) throws IOException {
        String host = "127.0.0.1.";
        int fileTransferPort = 6060;
        String username = "Yashoda";
        File file = new File(txtBrowse.getText());
        String fileName = file.getName();
        System.out.println("Filename: " + fileName);

        Socket socket = null;
        try {
            socket = new Socket(host, fileTransferPort);
            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(username);
            bw.newLine();
            bw.write(file.getName());
            bw.newLine();
            bw.flush();

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            System.out.println("Start: File uploading");
            while (true){
                byte[] buffer = new byte[1024];
                int len = bis.read(buffer);
                if (len == -1){
                    break;
                }
                bos.write(buffer, 0, len);
            }

            System.out.println("Finish: File uploading");
            bis.close();
        } catch (IOException e) {
            System.out.println("Connection failed");
            e.printStackTrace();
        }finally{
            socket.close();
        }


    }

    public void btnCancel(ActionEvent actionEvent) {
        txtBrowse.clear();
        txtBrowse.requestFocus();
        btnBrowse.requestFocus();
    }
}
