package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.FriendList;
import com.gui.domain.Friendship;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class SendFrReqController {
    private Stage stage;
    private UserService userService;
    private FriendshipService friendshipService;
    private Long idUser;
    private Long idSelected;
    private Long selectedButton=null;
    ObservableList<FriendList> model = FXCollections.observableArrayList();

    @FXML private ImageView backView;
    @FXML private ImageView sendFrReq;
    @FXML private TextField nume;
    @FXML private TableView<FriendList> tableView;
    @FXML private TableColumn<FriendList,?> fullName;
    @FXML private Pagination pag;
    @FXML private GridPane gridPane;
    private Page page;

    private Node create(Integer pageIndex) {
        gridPane.getChildren().clear();
        int n=0;
        for (var e: userService.getUsersOnPage(pageIndex))
        {
            Rectangle rr=new Rectangle(350,75);
            rr.setArcHeight(20);
            rr.setArcWidth(30);

            Button b=new Button(e.getFullName());
            b.setPrefSize(350, 75);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b.setOnMouseClicked(j->{
                    b.setStyle("-fx-background-color: #9DE3F2FF;" +
                            "-fx-cursor:hand;" +
                            "-fx-border-color: black;");
                    idSelected = e.getId();

            });
            b.setShape(rr);
            gridPane.add(b,0,n);
            n++;
        }
        return gridPane;
    }

    void init(Page page) {
        this.page=page;
        idUser = page.getIdUser();
//        model.setAll(getUserList());
        this.userService=page.getUserService();
        this.friendshipService=page.getFriendshipService();
        gridPane.setVgap(4);
        userService.setPageSize(4);
        int nrpag=(userService.nrOfUsers()-1)/4;
        if((userService.nrOfUsers()-1)%4!=0) nrpag++;
        pag.setMaxPageIndicatorCount(4);
        pag.setPageCount(nrpag);
        int finalNrpag = nrpag;
        pag.setPageFactory(pageIndex->{
            if (pageIndex >= finalNrpag) {
                return null;
            } else return create(pageIndex);
        });
    }

    @FXML
    public void initialize() {
        Tooltip.install(backView, new Tooltip("Go Back"));
        Tooltip.install(sendFrReq, new Tooltip("Send friend request"));
        nume.textProperty().addListener(o -> handleFilter());
    }

    private List<FriendList> getUserList() {
        List<FriendList> list = new ArrayList<>();
        userService.getAll().forEach(
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

    private void handleFilter() {
        Predicate<FriendList> p1 = n -> n.getFirstName().startsWith(nume.getText());
        Predicate<FriendList> p2 = n -> n.getLastName().startsWith(nume.getText());
        Predicate<FriendList> p3 = n -> n.getFullName().startsWith(nume.getText());

        model.setAll(getUserList()
                .stream()
                .filter(p1.or(p2).or(p3))
                .collect(Collectors.toList()));
        init(model);
        if(nume.getText().isEmpty())
            init(page);
    }

    private void init(ObservableList<FriendList> model) {
        gridPane.setVgap(4);
//        model.setAll(getUserList());
        int nrpag=(model.size())/4;
        if(model.size()%4!=0) nrpag++;
        pag.setMaxPageIndicatorCount(4);
        pag.setPageCount(nrpag);
        int finalNrpag = nrpag;
        pag.setPageFactory(pageIndex->{
            if (pageIndex >= finalNrpag) {
                return null;
            } else return create(pageIndex,model);
        });
    }

    private Node create(Integer pageIndex, ObservableList<FriendList> model) {
        gridPane.getChildren().clear();
        int n=0;
        for (var e:model)
        {
            Rectangle rr=new Rectangle(350,75);
            rr.setArcHeight(20);
            rr.setArcWidth(30);

            Button b=new Button(e.getFullName());
            b.setPrefSize(350, 75);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b.setOnMouseClicked(j->{
                b.setStyle("-fx-background-color: #9DE3F2FF;" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color: black;");
                idSelected = e.getUserId();

            });
            b.setShape(rr);
            gridPane.add(b,0,n);
            n++;
        }
        return gridPane;
    }

    public SendFrReqController() {
    }

    @FXML
    void SendFrRq(MouseEvent event) {
//        FriendList user = tableView.getSelectionModel().getSelectedItem();
        Friendship friend = new Friendship(idUser, idSelected);
        if (friendshipService.findOne(friend.getId()) != null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The friendship already exists!");
            alert.showAndWait();
            return;
        }
        Friendship test = friendshipService.addFriendship(friend);
        if (test.getU1() != -1L) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("The friendship already exists!");
            alert.showAndWait();
        } else if (test.getU1() == -1L) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("The friendship request was successfully sent!");
            alert.showAndWait();
        }
    }

    @FXML
    void Back(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("user.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        FriendshipsController controller = fxmlLoader.getController();
        controller.init(page);
    }

    @FXML
    public void unSelected(MouseEvent event) {
        init(page);
    }
}