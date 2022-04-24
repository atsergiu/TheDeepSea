package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.FriendList;
import com.gui.domain.Group;
import com.gui.domain.validators.GroupValidator;
import com.gui.domain.validators.MessageValidator;
import com.gui.domain.validators.UserValidator;
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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainPage {
    private Long id;
    @FXML private TextField bubbleName;
//    @FXML private TableView<?> bubbleTable;
    @FXML private ImageView events;
    @FXML private ImageView friendMenu;
    @FXML private ImageView logout;
    @FXML private TextField searchBar;
    @FXML private Label name;
    @FXML private ImageView newConv;
    @FXML private Label noConv;
    @FXML private Pane pane;
    @FXML public GridPane gridPane;
    @FXML private Pagination pag;
    private Page page;
    ObservableList<Group> model = FXCollections.observableArrayList();

    private MessageService messageService;
    private UserService userService;

    public MainPage() {
        
    }

    @FXML
    public void initialize() {
        searchBar.textProperty().addListener(o -> handleFilter());
        Tooltip.install(events,new Tooltip("Events"));
        Tooltip.install(logout,new Tooltip("Logout"));
        Tooltip.install(newConv,new Tooltip("New Bubble"));
        Tooltip.install(friendMenu,new Tooltip("Friends Menu"));
    }

    private GridPane create(int page)
    {
        gridPane.getChildren().clear();
        int n=0;
        for (var i : messageService.getMessagesOnPage(id, page)) {
            Group gr=messageService.findGroup(i);
            Button b=new Button(gr.getName());
            b.setPrefSize(350, 70);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                   "-fx-cursor:hand;");

            Rectangle rr=new Rectangle(350,70);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            b.setShape(rr);
            b.setOnAction(e->{
                FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("bubblePage.fxml"));
                var stage = (Stage) logout.getScene().getWindow();
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
                BubblePage controller=fxmlLoader.getController();

                controller.init(this.page, gr.getId());
                stage.setScene(scene);
            });
            gridPane.add(b, 0, n);
            n++;
        }

        return gridPane;
    }

    public void init(Page page) {
        this.page=page;
        this.id =page.getIdUser();
        this.messageService=page.getMessageService();
        this.userService=page.getUserService();
        gridPane.setVgap(5);
        int n;
        name.setText(page.getFullName());
        messageService.setPageSize(4);
        int  nrpag=messageService.nrOfConversations(id)/4;
        if(messageService.nrOfConversations(id)%4!=0) nrpag++;
        if(nrpag>0)
        {
            noConv.setOpacity(0);
            pag.setMaxPageIndicatorCount(4);
            pag.setPageCount(nrpag);
            int finalNrpag = nrpag;
            pag.setPageFactory(pageIndex->{
                if (pageIndex >= finalNrpag) {
                    return null;
                } else return create(pageIndex);
            });
        }else {
            pag.setOpacity(0);
        }

    }

    private void handleFilter() {

        Predicate<Group> p1=n->n.getName().startsWith(searchBar.getText());

        model.setAll(messageService.getGroups(id)
                .stream()
                .filter(p1)
                .collect(Collectors.toList()));

        init(model);
        if(searchBar.getText().isEmpty())
            init(page);
    }

    private void init(ObservableList<Group> model) {
        gridPane.setVgap(5);

        int  nrpag=model.size()/4;
        if(model.size()%4!=0) nrpag++;
        if(nrpag>0)
        {
            noConv.setOpacity(0);
            pag.setMaxPageIndicatorCount(4);
            pag.setPageCount(nrpag);
            int finalNrpag = nrpag;
            pag.setPageFactory(pageIndex->{
                if (pageIndex >= finalNrpag) {
                    return null;
                } else return create(pageIndex,model);
            });
        }else {
            pag.setOpacity(0);
        }
    }

    private Node create(Integer pageIndex, ObservableList<Group> model) {
        gridPane.getChildren().clear();
        int n=0;
        for (var gr : model) {
            Button b=new Button(gr.getName());
            b.setPrefSize(350, 70);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");

            Rectangle rr=new Rectangle(350,70);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            b.setShape(rr);
            b.setOnAction(e->{
                FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("bubblePage.fxml"));
                var stage = (Stage) logout.getScene().getWindow();
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
                BubblePage controller=fxmlLoader.getController();

                controller.init(this.page, gr.getId());
                stage.setScene(scene);
            });
            gridPane.add(b, 0, n);
            n++;
        }

        return gridPane;
    }


    @FXML
    void friendMenu(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("user.fxml"));
        var stage = (Stage) logout.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
        FriendshipsController controller=fxmlLoader.getController();
        controller.init(page);
        stage.show();
    }

    @FXML
    void newBubble(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("newBubble.fxml"));
        var stage = (Stage) logout.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        NewBubble controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

    @FXML
    void openLogout(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("login.fxml"));
        var stage = (Stage) logout.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(scene);
    }

    @FXML
    void raport(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("raportOne.fxml"));
        var stage = (Stage) logout.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        RaportOne controller=fxmlLoader.getController();
        controller.init(page,null);
        stage.setScene(scene);
    }

    @FXML
    public void openEvents(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("events.fxml"));
        var stage = (Stage) events.getScene().getWindow();
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
