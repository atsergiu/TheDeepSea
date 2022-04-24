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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FriendReq  implements Observer<EventType> {

    Stage stage;
    private FriendshipService friendshipService;
    private UserService userService;
    ObservableList<FriendList> modelFriendship = FXCollections.observableArrayList();
    private Long idUser;

    @FXML private ImageView accept;
    @FXML private ImageView decline;
    @FXML private ImageView exitButtonClick;
    @FXML private ImageView history;
    @FXML private ImageView unsend;
    @FXML private Pagination pag;
    @FXML private GridPane gridPane;
    private Long idSelected;

    @FXML private TableView<FriendList> tableView;
    @FXML private TableColumn<?,?> firstName;
    @FXML private TableColumn<?,?> lastName;
    @FXML private TableColumn<?,?> date;
    @FXML private TableColumn<?,?> status;
    private Page page;
    private GridPane create(int page)
    {
        gridPane.getChildren().clear();
        int n=0;
        for(var e :userService.getFriendsOnPage(idUser,page,"Pending"))
        {
            Button b;
            b=new Button(e.getFullName());
            b.setPrefSize(200, 70);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b.setOnAction(j->{
                idSelected= e.getId();
            });

            Tuple<Long,Long> aa=new Tuple<>(idUser,e.getId());
            Friendship fr=friendshipService.findOne(aa);

            Rectangle rr=new Rectangle(130,70);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            b.setShape(rr);
            gridPane.add(b, 0, n);

            Button bn;
            if(fr.getU1()==idUser) {
                bn=new Button("Sent: "+fr.getDate());
            }
            else {
                bn=new Button("Pending: "+fr.getDate());
            }
            bn.setPrefSize(200, 70);
            bn.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            bn.setOnAction(j->{
                bn.setStyle("-fx-background-color: #9DE3F2FF;"+
                        "-fx-cursor:hand;"+
                        "-fx-border-color: black;");
                idSelected= e.getId();
            });
            bn.setShape(rr);
            gridPane.add(bn, 1, n);

            n++;
        }
        return gridPane;
    }

    void init(Page page){
        this.page=page;
        this.idUser=page.getIdUser();
        this.userService=page.getUserService();
        this.friendshipService=page.getFriendshipService();
        gridPane.setVgap(4);
        gridPane.setHgap(2);

        int n;
        //name.setText(userService.findOne(id).getFullName());
        userService.setPageSize(5);
        int  nrpag=friendshipService.getAllFriendshipOfAUser(idUser,"Pending").size()/5;
        if(friendshipService.getAllFriendshipOfAUser(idUser,"Pending").size()%5!=0) nrpag++;
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


        Tooltip.install(exitButtonClick,new Tooltip("Go Back"));
        Tooltip.install(accept,new Tooltip("Accept a friend request"));
        Tooltip.install(decline,new Tooltip("Decline a friend request"));
        Tooltip.install(history,new Tooltip("See the history of friend requests"));
        Tooltip.install(unsend,new Tooltip("Unsend a friend request"));
    }

    public FriendReq() {
    }


    @FXML
    void  ExitButtonClick(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("user.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        FriendshipsController controller=fxmlLoader.getController();
        controller.init(page);
    }

    private void changeStatus(String st)
    {
        var fr=friendshipService.changeStatus(idUser,idSelected,st);
        init(page);
    }

    @FXML

    void acceptButton()
    {
            changeStatus("Accepted");
    }


    @FXML
    void declineButton()
    {
        changeStatus("Denied");
    }


    private List<FriendList> get() {
        var list = new ArrayList<FriendList>();
        friendshipService.getAllFriendshipOfAUser(idUser,"Pending").forEach(
                e -> {
                    if (e.getU1() != idUser) {
                        User usr = userService.findOne(e.getU1());
                        list.add(new FriendList( usr.getFirstName(), usr.getLastName(),e.getDate(),usr.getId(), e.getStatus()));
                    } else {
                        User usr = userService.findOne(e.getU2());
                        list.add(new FriendList( usr.getFirstName(), usr.getLastName(),e.getDate(),usr.getId(), "Sent"));
                    }
                }
        );
        return list;
    }

    @FXML
    void historyButton(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("historyFriendReq.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        HistoryFrendReq controller=fxmlLoader.getController();
        controller.init(page);
    }

    @FXML
    void unsendButton() {
        Tuple<Long, Long> id = new Tuple<>(idUser,idSelected);
        friendshipService.deleteFriendship(id);
        init(page);
    }

    @Override
    public void update(EventType eventType) {
        modelFriendship.setAll(get());
    }

    public void unselect(MouseEvent event) {
        init(page);
    }
}
