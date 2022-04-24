package com.gui.controller;

import com.gui.domain.Event;
import com.gui.domain.FriendList;
import com.gui.domain.validators.EventValidator;
import com.gui.domain.validators.FriendValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.repository.database.EventDbRepository;
import com.gui.repository.database.FriendshipDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.EventService;
import com.gui.service.FriendshipService;
import com.gui.service.UserService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Notifications {
    private Stage stage;
    private UserService userService;
    private EventService eventService;
    private Long idUser;
    ObservableList<String > model = FXCollections.observableArrayList();

    @FXML private TableView<String> tableView;
    @FXML private TableColumn<String,String>  notification;

    public Notifications() {
        EventDbRepository eventDbRepository=new EventDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new EventValidator());
        UserDbRepository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new UserValidator());
        userService=new UserService(userDbRepository);
        eventService=new EventService(eventDbRepository,userService);
    }

    public void init(Long id)
    {
        idUser=id;
        //notification.setCellValueFactory(new PropertyValueFactory<>("notiifcation"));
        model.setAll(getEvents());
        notification.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        tableView.setItems(model);
    }


    private List<String> getEvents()
    {
        List<String> list=new ArrayList<>();
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        eventService.getNotification(idUser).forEach(
                e->{
                    Event event=eventService.findOne(e);
                    event.setId(e);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Date firstDate = null;
                    try {
                        firstDate = sdf.parse(event.getDate());
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    Date secondDate= new Date();
                    long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                    if(diff<=7)
                        list.add(event.getName()+" is in "+diff+" days!");
                }
        );
        return list;
    }
}
