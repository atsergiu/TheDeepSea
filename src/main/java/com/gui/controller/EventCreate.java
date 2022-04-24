package com.gui.controller;
import com.gui.MainGUI;
import com.gui.domain.Event;
import com.gui.domain.validators.EventValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.domain.validators.ValidationException;
import com.gui.repository.database.EventDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.EventService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class EventCreate {
    private Stage stage;
    private static Long id;
    EventService eventService;
    UserService userService;
    private Long idUser;

    @FXML private TextField textName;
    @FXML private TextField textLocation;
    @FXML private TextArea textDesc;
    @FXML private DatePicker textDate;
    @FXML private ImageView exit;
    @FXML private ImageView create;
    private Page page;
    public EventCreate() {
    }

    void init(Page page)
    {
        this.page=page;
        idUser=page.getIdUser();
        this.eventService=page.getEventService();
        this.userService=page.getUserService();
    }

    public void createButton(MouseEvent event) {
        int ok=0;
        if(textDate.getValue().toString().isEmpty())
        {
            ok=1;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Date can not be null!");
            alert.showAndWait();
        }


        try {
            eventService.createEvent(new Event(idUser, textName.getText(), textDesc.getText(), textLocation.getText(), textDate.getValue().toString(), new ArrayList<>()));
        }catch (ValidationException e)
        {
            ok=1;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        if(ok==0)
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Your event has been created successfully!");
            alert.showAndWait();

            backButton(event);
        }
    }

    public void backButton(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("events.fxml"));
        stage = (Stage) create.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventMain controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

}
