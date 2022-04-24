package com.gui;

import com.gui.controller.LoginController;
import com.gui.controller.MainPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.pdfbox.pdmodel.PDDocument;


import java.io.IOException;

public class MainGUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
          //FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("login.fxml"));
            Parent fxmlLoader=FXMLLoader.load(getClass().getResource("login.fxml"));
            //Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            Scene scene = new Scene(fxmlLoader);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}