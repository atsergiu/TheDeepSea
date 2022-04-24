package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.Event;
import com.gui.domain.FriendList;
import com.gui.domain.User;
import com.gui.domain.validators.EventValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.events.EventInterface;
import com.gui.events.EventType;
import com.gui.observer.Observable;
import com.gui.observer.Observer;
import com.gui.repository.database.EventDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.EventService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventPage  {
    private Stage stage;
    EventService eventService;
    UserService userService;
    private Long idUser;
    private Long idEvent;
    ObservableList<FriendList> model = FXCollections.observableArrayList();

    @FXML private ImageView exit;
    @FXML private TableView friendsTable;
    @FXML private TableColumn fullName;
    @FXML private Label name;
    @FXML private Label date;
    @FXML private Label creator;
    @FXML private Label howMany;
    @FXML private Label loc;
    @FXML private ScrollPane description;
    @FXML private Button notification;
    private Page page;
    public EventPage() {

    }

    public void create() {
        Event event = eventService.findOne(idEvent);
        name.setText(event.getName());
        date.setText(event.getDate());
        loc.setText(event.getLocation());
        User user = userService.findOne(event.getCreator());
        creator.setText("Event made by\n" + user.getFullName());
        howMany.setText(event.getParticipants().size() + " are going");
        description.setContent(new Label(event.getDescription()));
//        Rectangle rr=new Rectangle(124,164);
//        rr.setArcHeight(20);
//        rr.setArcWidth(30);
//        description.setShape(rr);
    }

    public void init(Page page, Long idEvent)
    {   this.page=page;
        this.idUser=page.getIdUser();
        this.idEvent=idEvent;
        this.eventService=page.getEventService();
        this.userService=page.getUserService();

        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        model.setAll(getFRiendsList());
        friendsTable.setItems(model);
        create();

        if(eventService.getNotification(idUser).contains(idEvent))
        {   notification.setOpacity(1);
            notification.setText("Notifications off");

        }else {
            if(eventService.findOne(idEvent).getParticipants().contains(idUser))
            {
                notification.setOpacity(1);
                notification.setText("Notifications on");
            }else
            {
                notification.setOpacity(0);
            }
        }

    }


    private List<FriendList> getFRiendsList() {
        List<FriendList> list = new ArrayList<>();
        eventService.getParticipants(idEvent).forEach(
                e -> {

                    if (e.getId() != idUser) {
                        User usr = userService.findOne(e.getId());
                        usr.setId(e.getId());
                        list.add(new FriendList(usr.getId(), usr.getFirstName(), usr.getLastName(), "-", usr.getGender()));
                    }
                }
        );


        return list;
    }

    public void backButton(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("events.fxml"));
        stage = (Stage) exit.getScene().getWindow();
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

    @FXML
    public void change(MouseEvent event) {
        if(notification.getText().equals("Notifications off"))
        {
            eventService.setNotification(idEvent,idUser,0);
            notification.setText("Notifications on");
        }else {
            eventService.setNotification(idEvent,idUser,1);
            notification.setText("Notifications off");
        }
    }

    @FXML
    public void goingButton(MouseEvent event) {
        if(eventService.findOne(idEvent).getParticipants().contains(idUser))
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("You are already subscribed to this event!");
            alert.showAndWait();

        }else {
            eventService.addParticipant(idEvent, idUser);
            init(page,idEvent);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("You are now subscribed to this event!");
            alert.showAndWait();
        }
    }

    @FXML
    public void notGoingButton(MouseEvent event) {
        if(eventService.findOne(idEvent).getParticipants().contains(idUser))
        {
            eventService.removeParticipant(idEvent,idUser);
            init(page,idEvent);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("You are no longer attending this event!");
            alert.showAndWait();

        }else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setContentText("You are not attending this event!");
            alert.showAndWait();
        }
    }
}
