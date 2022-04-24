package com.gui.controller;
import com.gui.MainGUI;
import com.gui.domain.FriendList;
import com.gui.domain.Friendship;
import com.gui.domain.Tuple;
import com.gui.domain.User;
import com.gui.domain.validators.FriendValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.events.EventType;
import com.gui.observer.Observer;
import com.gui.repository.database.FriendshipDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.FriendshipService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FriendshipsController implements Observer<EventType> {
    private Stage stage;
    private static Long id;
    private UserService userService;
    private FriendshipService friendshipService;
    private Long idSelected;
    ObservableList<FriendList> modelFriends = FXCollections.observableArrayList();

    @FXML private TableColumn<?, ?> date;
    @FXML private TableColumn<?, ?> firstName;
    @FXML private TableColumn<?, ?> lastName;
    @FXML private TableColumn<?, ?> userId;
    @FXML private TableView<FriendList> table;
    @FXML private ImageView deleteView;
    @FXML private ImageView logoutView;
    @FXML private ImageView friendView;
    @FXML private ImageView sendView;
    @FXML private Pagination pag;
    @FXML public GridPane gridPane;
    @FXML private ImageView raport;
    private Page page;


    @FXML
    void logout() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("mainPage.fxml"));
        stage = (Stage) logoutView.getScene().getWindow();
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
    void openFrReq() {

        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("sendFrReq.fxml"));
        stage = (Stage) sendView.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendFrReqController controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);

    }

    @FXML
    void openFriendReqList() {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("friendreq.fxml"));
        stage = (Stage) friendView.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        FriendReq controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

    public FriendshipsController() {


    }


    private List<FriendList> get() {
        var list = new ArrayList<FriendList>();
        friendshipService.getAllFriendshipOfAUser(id,"Accepted").forEach(
                e -> {
                    if (e.getU1() != id) {
                        User usr = userService.findOne(e.getU1());
                        list.add(new FriendList(usr.getId(), usr.getFirstName(), usr.getLastName(), e.getDate()));
                    } else {
                        User usr = userService.findOne(e.getU2());
                        list.add(new FriendList(usr.getId(), usr.getFirstName(), usr.getLastName(), e.getDate()));
                    }
                }
        );
        return list;
    }

    @FXML
    void deleteFriend() {
        Tuple<Long, Long> idFriendship = new Tuple<>(id, idSelected);
        var friendship = friendshipService.findOne(idFriendship);
        friendshipService.deleteFriendship(friendship.getId());
        init(page);
    }

    private GridPane create(int page)
    {
        gridPane.getChildren().clear();
        int n=0;
        for(var e :userService.getFriendsOnPage(id,page,"Accepted"))
        {
            Tuple<Long,Long> aa=new Tuple<>(id,e.getId());
            Friendship fr=friendshipService.findOne(aa);
            Button b;
            b=new Button(e.getFullName()+"\nFriends since: "+fr.getDate());
            b.setPrefSize(350, 70);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;" +
                    "-fx-alignment:center;");
            b.setOnAction(j->{
                b.setStyle("-fx-background-color: #9DE3F2FF;"+
                        "-fx-cursor:hand;" +
                        "-fx-alignment:center;"+
                        "-fx-border-color: black;");
                idSelected= e.getId();
            });


            Rectangle rr=new Rectangle(350,70);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            b.setShape(rr);

            gridPane.add(b, 0, n);
            n++;
        }
        return gridPane;
    }

    public void init(Page page) {
        this.page=page;
        this.id = page.getIdUser();
        this.userService=page.getUserService();
        this.friendshipService=page.getFriendshipService();
        this.friendshipService.addObserver(this);
        gridPane.setVgap(5);
        int n;
        //name.setText(userService.findOne(id).getFullName());
        userService.setPageSize(5);
        int  nrpag=friendshipService.getAllFriendshipOfAUser(id,"Accepted").size()/5;
        if(friendshipService.getAllFriendshipOfAUser(id,"Accepted").size()%5!=0) nrpag++;
        int finalNrpag = nrpag;
        if(nrpag>0)
        {
            pag.setMaxPageIndicatorCount(5);
            pag.setPageCount(nrpag);
            pag.setPageFactory(pageIndex->{
                if (pageIndex >= finalNrpag) {
                    return null;
                } else return create(pageIndex);
            });
        }else {
            pag.setOpacity(0);
        }
        
        Tooltip.install(logoutView, new Tooltip("Go back"));
        Tooltip.install(friendView, new Tooltip("Open your Friend Requests"));
        Tooltip.install(sendView, new Tooltip("Send a friend request"));
        Tooltip.install(deleteView, new Tooltip("Delete a friend"));
    }

    @Override
    public void update(EventType eventType) {
        modelFriends.setAll(get());
    }

    @FXML
    void raport(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("raportOne.fxml"));
        stage = (Stage) sendView.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RaportOne controller=fxmlLoader.getController();
        controller.init(page,idSelected);
        stage.setScene(scene);
    }

    public void unselect(MouseEvent event) {
        init(page);
    }
}
