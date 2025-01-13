package lk.ijse.dep13.remote.server.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lk.ijse.dep13.remote.server.ServerAppInitializer;


import java.io.*;


public class FileReceiverSceneController   {
    public AnchorPane root;
    public Label lblFileReceiver;
    public TextField txtLocation;
    public Button btnLocation;
    public Button btnSave;
    public Button btnCancel;

    public void btnLocation(ActionEvent actionEvent) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");


        File selectedDirectory = directoryChooser.showDialog(root.getScene().getWindow());


        if (selectedDirectory != null) {
            txtLocation.setText(selectedDirectory.getAbsolutePath());
            ServerAppInitializer.path = selectedDirectory.getAbsolutePath();
        }
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {

        String folderPath = txtLocation.getText();

        System.out.println("????????????????????????????"+folderPath);
        if (folderPath.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Directory Not Selected");
            alert.setContentText("Please select a valid directory before saving the file.");
            alert.showAndWait();
            return;

        }



        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();



    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        txtLocation.clear();
        txtLocation.requestFocus();
        btnLocation.requestFocus();
    }

}
