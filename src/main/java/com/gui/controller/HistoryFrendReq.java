package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.FriendList;
import com.gui.domain.User;
import com.gui.domain.validators.FriendValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.repository.database.FriendshipDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.FriendshipService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryFrendReq {
    Stage stage;
    private FriendshipService friendshipService;
    private UserService userService;
    private Long idUser;
    ObservableList<FriendList> modelFriendship = FXCollections.observableArrayList();
    @FXML
    private ImageView back;
    @FXML private TableView<FriendList> tableView;
    @FXML private TableColumn<?,?> firstName;
    @FXML private TableColumn<?,?> lastName;
    @FXML private TableColumn<?,?> status;
    @FXML private TableColumn<?,?> date;
    private Page page;
    public HistoryFrendReq() {}



    void init(Page page){
        idUser=page.getIdUser();
        this.page=page;
        this.friendshipService=page.getFriendshipService();
        this.userService=page.getUserService();

        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));
        modelFriendship.setAll(get());
        tableView.setItems(modelFriendship);
        Tooltip.install(back,new Tooltip("Go Back"));
    }

    private List<FriendList> get(){
        var list = new ArrayList<FriendList>();
        friendshipService.getAll().forEach(
                e-> {
                    if (e.getU1() != idUser) {
                        User usr = userService.findOne(e.getU1());
                        list.add(new FriendList( usr.getFirstName(), usr.getLastName(), e.getDate(),usr.getId(),e.getStatus()));
                    } else {
                        User usr = userService.findOne(e.getU2());
                        list.add(new FriendList( usr.getFirstName(), usr.getLastName(), e.getDate(),usr.getId(),e.getStatus()));
                    }
                }
        );
        return list;
    }

    @FXML
    void backButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("friendreq.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        FriendReq controller=fxmlLoader.getController();
        controller.init(page);
    }
}
