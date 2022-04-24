package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.validators.UserValidator;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.Page;
import com.gui.service.UserService;
import com.gui.utils.PasswordAuthentication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;



public class LoginController {
    @FXML private TextField textEmail;
    @FXML private PasswordField textPassword;
    @FXML private ImageView signUpButtonClick;
    @FXML private ImageView loginButtonClick;
    @FXML private SplitPane split;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private UserService userService;

    public LoginController() {
        UserDbRepository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new UserValidator());

        this.userService = new UserService(userDbRepository);
    }

    @FXML
    public void initialize(){
        Tooltip.install(signUpButtonClick,new Tooltip("Sign up"));
        Tooltip.install(loginButtonClick,new Tooltip("Login"));
    }

    @FXML
    public Stage loginButtonClick() throws IOException {
        var user = userService.findOneEmail(textEmail.getText());
        PasswordAuthentication pass=new PasswordAuthentication();
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The account doesn't exist");
            alert.showAndWait();
        } else if (!pass.authenticate(textPassword.getText(),user.getPassword())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Wrong password!");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("mainPage.fxml"));
                stage = (Stage) signUpButtonClick.getScene().getWindow();
                Scene scene = new Scene(fxmlLoader.load());
                stage.setScene(scene);
                MainPage controller=fxmlLoader.getController();
                controller.init(new Page(user.getId()));
                stage.show();
                return stage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void signUpButtonClick() {
            FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("signUp.fxml"));
            stage = (Stage) signUpButtonClick.getScene().getWindow();
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            stage.setScene(scene);
    }
    public void init(){
        Tooltip.install(signUpButtonClick,new Tooltip("Sign up"));
        Tooltip.install(loginButtonClick,new Tooltip("Login"));

    }
}
