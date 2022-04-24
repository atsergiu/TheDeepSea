package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.*;
import com.gui.domain.validators.FriendValidator;
import com.gui.domain.validators.GroupValidator;
import com.gui.domain.validators.MessageValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.repository.Repository;
import com.gui.repository.database.FriendshipDbRepository;
import com.gui.repository.database.GroupDbRepository;
import com.gui.repository.database.MessageDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.MessageService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class NewBubble {

    private Stage stage;
    @FXML
    private ImageView add;

    @FXML
    private TableView<User> addList;

    @FXML
    private TextField bubbleName;

    @FXML
    private ImageView delete;

    @FXML
    private TableView<User> finalList;

    @FXML
    private ImageView finish;

    @FXML
    private TableColumn<?, ?> firstName1;

    @FXML
    private TableColumn<?, ?> firstName2;

    @FXML
    private TableColumn<?, ?> lastName1;

    @FXML
    private TableColumn<?, ?> lastName2;

    @FXML
    private TextField user;
    private Page page;
    private UserService userService;
    private GroupDbRepository group;
    private Long id;
    private List<User> finalUsers=new ArrayList<>();
    private MessageService messageService;
    ObservableList<User> modelAdd = FXCollections.observableArrayList();
    ObservableList<User> modelFinal = FXCollections.observableArrayList();

    public NewBubble() {
    }

    @FXML
    public void initialize() {
    }

    private void handleFilter() {
        Predicate<User> p1 = n -> n.getFirstName().startsWith(user.getText());
        Predicate<User> p2 = n -> n.getLastName().startsWith(user.getText());

        modelAdd.setAll(StreamSupport
                .stream(getUserList().spliterator(), false)
                .filter(p1.or(p2))
                .collect(Collectors.toList()));
    }
    @FXML
    private void add(){
        User person = addList.getSelectionModel().getSelectedItem();
        if(person==null)
            return;
        if(!finalUsers.contains(person)) {
            finalUsers.add(person);
            modelFinal.setAll(finalUsers);
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Utilizatorul deja a fost adaugat!");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void delete(){
        User person = finalList.getSelectionModel().getSelectedItem();
        if(person==null)
            return;
        finalUsers.remove(person);
        modelFinal.setAll(finalUsers);
    }

    @FXML
    private void finish(){
        var bubble=bubbleName.getText();

        if(bubble==null || bubble.equals("")||bubble.trim().isEmpty()||bubble.startsWith(" "))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The name of the conversation must be correct");
            alert.showAndWait();
            return;
        }
        if(bubble.length()>20)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The name of the conversation can have a maximum of 20 characters");
            alert.showAndWait();
            return;
        }
        ArrayList<Long> idUsers=new ArrayList<>();
        finalUsers.forEach(user->{idUsers.add(user.getId());});
        if(idUsers.size()==0)
            return;
        idUsers.add(id);
        messageService.createGroup(idUsers,bubble);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Yey!");
        alert.setContentText("The group was successfully created!");
        alert.showAndWait();
        Back();
    }

    @FXML
    private void Back(){
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("mainPage.fxml"));
        stage = (Stage) add.getScene().getWindow();
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


    public void init(Page page) {
        this.page=page;
        this.id = page.getIdUser();
        this.messageService=page.getMessageService();
        this.userService=page.getUserService();
        modelAdd.setAll(getUserList());
        Tooltip.install(add,new Tooltip("Add selected user to Bubble"));
        Tooltip.install(delete,new Tooltip("Delete selected user from Bubble"));
        Tooltip.install(finish,new Tooltip("Finish the Bubble"));
        firstName1.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstName2.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName2.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addList.setItems(modelAdd);
        finalList.setItems(modelFinal);
        user.textProperty().addListener(o -> handleFilter());
    }


    private List<User> getUserList() {
        List<User> list = new ArrayList<>();
        userService.getAll().forEach(
                e -> {
                    if (e.getId() != id) {
                        User usr = userService.findOne(e.getId());
                        usr.setId(e.getId());
                        list.add(usr);
                    }
                }
        );
        return list;
    }
}
