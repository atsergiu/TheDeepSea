package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.User;
import com.gui.domain.validators.FriendValidator;
import com.gui.domain.validators.GroupValidator;
import com.gui.domain.validators.MessageValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.repository.Repository;
import com.gui.repository.database.FriendshipDbRepository;
import com.gui.repository.database.GroupDbRepository;
import com.gui.repository.database.MessageDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.FriendshipService;
import com.gui.service.MessageService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class RaportOne {
    private FriendshipService friendshipService;
    private Stage stage;
    @FXML
    private ImageView back;
    @FXML
    private Label text;
    @FXML
    private DatePicker date1;
    @FXML
    private Label error;
    @FXML
    private DatePicker date2;

    @FXML
    private ImageView done;
    private Page page;

    public RaportOne() {

    }

    @FXML
    private ImageView pdf;
    private Long idUser;
    private Long idFriend;
    private MessageService messageService;

    @FXML
    void back(MouseEvent event) {
        if(idFriend==null)
            backMain(event);
        else backFriend(event);
    }

    @FXML
    void backMain(MouseEvent event) {
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(MainGUI.class.getResource("mainPage.fxml"));
        stage = (Stage) back.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainPage controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

    @FXML
    void backFriend(MouseEvent event) {
        FXMLLoader fxmlLoader;
        fxmlLoader = new FXMLLoader(MainGUI.class.getResource("user.fxml"));
        stage = (Stage) back.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FriendshipsController controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

    @FXML
    void done(MouseEvent event) {
        var start = date1.getValue();
        var end = date2.getValue();
        if (start == null || end == null || start.compareTo(end) > 0) {
            error.setText("The given dates are not correct!");
            return;
        }
        String st;
        if (idFriend == null) {
            st = messageService.raportFriendsText(idUser, friendshipService, start, end);
            error.setText("");
        } else {
            st = messageService.raport2Text(idUser, idFriend, start, end);
            error.setText("");
        }
        text.setText(st);
    }

    @FXML
    void pdf(MouseEvent event) {
        var start = date1.getValue();
        var end = date2.getValue();
        if (start == null || end == null || start.compareTo(end) > 0) {
            error.setText("The given dates are not correct!");
            return;
        }
        Stage secondStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF Files", ".pdf"));
        File location = fileChooser.showSaveDialog(secondStage);
        if(location==null)
            return;
        if (idFriend == null) {
            messageService.raportFriends(idUser, friendshipService, start, end, location);
            error.setText("");
        } else {
            messageService.raport2(idUser, idFriend, start, end, location);
            error.setText("");
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Created");
        alert.setContentText("The PDF Document was created!");
        alert.showAndWait();
    }

    void init(Page page, Long idFriend) {
        this.page = page;
        this.messageService = page.getMessageService();
        this.friendshipService = page.getFriendshipService();
        this.idUser = page.getIdUser();
        this.idFriend = idFriend;
    }

}
