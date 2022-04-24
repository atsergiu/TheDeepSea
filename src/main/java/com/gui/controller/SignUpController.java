package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.User;
import com.gui.domain.validators.UserValidator;
import com.gui.domain.validators.ValidationException;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.UserService;
import com.gui.utils.PasswordAuthentication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpController {

    @FXML private TextField textFirstName;
    @FXML private TextField textLastName;
    @FXML private TextField textEmail;
    @FXML private TextField textNewPassword;
    @FXML private TextField textReEnterPassword;
    @FXML private ComboBox textGender;

    private Stage stage;
    private static Long id;
    private UserService userService;

    public SignUpController() {
        UserDbRepository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new UserValidator());
        this.userService = new UserService(userDbRepository);
    }

    @FXML
    void createButton(MouseEvent event) {
        int ok=0;
        if(textNewPassword.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("You need a password!");
            alert.showAndWait();
            ok=1;
        }
        if (!textNewPassword.getText().equals(textReEnterPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The passwords don't match");
            alert.showAndWait();
            ok=1;
        }
        try {
            PasswordAuthentication pass = new PasswordAuthentication();
            User usr;
            usr = userService.addUtilizator(new User(textFirstName.getText(),textLastName.getText(), textGender.getValue().toString(), textEmail.getText(), pass.hash(textNewPassword.getText())));
            if (usr != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("This user already exists!");
                alert.showAndWait();
                ok=1;
            }
        } catch (ValidationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            ok=1;
        } catch (NullPointerException x)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The gender can not be null!");
            alert.showAndWait();
            ok=1;
        }
        if(ok==0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Your account has been created successfully!");
            alert.showAndWait();

        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        }
    }

    public void backButton(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("login.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
    }
}
