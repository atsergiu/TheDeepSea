package com.gui.controller;
import com.gui.MainGUI;
import com.gui.domain.Event;
import com.gui.domain.FriendList;
import com.gui.domain.User;
import com.gui.domain.validators.EventValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.events.EventType;
import com.gui.observer.Observer;
import com.gui.repository.database.EventDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.EventService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.xml.transform.Source;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventMain implements Observer<EventType> {
    private Stage stage;
    EventService eventService;
    UserService userService;
    private Long idUser;
    private Long idSelected;
    ObservableList<Event> model = FXCollections.observableArrayList();
    ObservableList<String > modelTabel = FXCollections.observableArrayList();

    @FXML private ImageView logoutView;
    @FXML private  ImageView create;
    @FXML private ImageView notificationBell;
    @FXML private TableView<String> tableView;
    @FXML private TableColumn<String,String>  notification;
    @FXML private Pagination pag;
    @FXML private GridPane grid;
    @FXML private TextField searchBar;
    @FXML private CheckBox goingFiltru;
    private Page page;


    public EventMain() {
    }

    private Node create(int pageIndex)
    {
        grid.getChildren().clear();
        int n=0;
        for(var e:eventService.getUsersOnPage(pageIndex))
        {
            Rectangle rr=new Rectangle(175,65);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            Button b=new Button(e.getName());
            b.setPrefSize(175, 65);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b.setOnAction(j->{
                FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("eventPage.fxml"));
                var stage = (Stage) create.getScene().getWindow();
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
                EventPage controller=fxmlLoader.getController();
                controller.init(page, e.getId());
                stage.setScene(scene);
            });

            Button b1=new Button(e.getDate());
            b1.setPrefSize(175, 65);
            b1.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
//            b1.setOnAction(j->{
////                b.setStyle("-fx-background-color: #9DE3F2FF;"+
////                        "-fx-cursor:hand;"+
////                        "-fx-border-color: black;");
//                idSelected= e.getId();
//            });
            b.setShape(rr);
            b1.setShape(rr);
            grid.add(b,0,n);
            grid.add(b1,1,n);
            n++;
        }
        return grid;
    }



    void init(Page page) {
        this.page=page;
        idUser = page.getIdUser();
        this.userService=page.getUserService();
        this.eventService=page.getEventService();
        grid.setVgap(4);
        grid.setHgap(2);
        eventService.setPageSize(4);
        int nrpag=eventService.numbOfEvents()/4;
        if(eventService.numbOfEvents()%4!=0) nrpag++;
        pag.setMaxPageIndicatorCount(4);
        pag.setPageCount(nrpag);
        int finalNrpag = nrpag;
        pag.setPageFactory(pageIndex->{
            if (pageIndex >= finalNrpag) {
                return null;
            } else return create(pageIndex);
        });
        modelTabel.setAll(getEvents());
        notification.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        tableView.setItems(modelTabel);
        tableView.setVisible(false);

    }

    @FXML
    public void initialize() {
        searchBar.textProperty().addListener(o->handleFilter());
        goingFiltru.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue){

                    model.setAll(getGoing());
                    init(model);

                }else{

                    init(page);
                }
            }
        });

        notificationBell.onMouseEnteredProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tableView.setVisible(true);
            }
        });
        notificationBell.onMouseExitedProperty().set(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                tableView.setVisible(false);
            }
        });

    }

    private void handleFilter() {
        Predicate<Event> p1=n->n.getName().startsWith(searchBar.getText());
        model.setAll(getEventList()
                .stream()
                .filter(p1)
                .collect(Collectors.toList()));
        init(model);
        if(searchBar.getText().isEmpty())
            init(page);
    }

    private Node create(Integer pageIndex, ObservableList<Event> model) {
        grid.getChildren().clear();
        int n=0;
        for(var e:model)
        {
            Rectangle rr=new Rectangle(175,65);
            rr.setArcHeight(20);
            rr.setArcWidth(30);
            Button b=new Button(e.getName());
            b.setPrefSize(175, 70);
            b.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b.setOnAction(j->{
                FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("eventPage.fxml"));
                var stage = (Stage) create.getScene().getWindow();
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
                EventPage controller=fxmlLoader.getController();
                controller.init(page, e.getId());
                stage.setScene(scene);
            });

            Button b1=new Button(e.getDate());
            b1.setPrefSize(175, 70);
            b1.setStyle("-fx-background-color: #9DE3F2FF;"+
                    "-fx-cursor:hand;");
            b1.setOnAction(j->{
//                b.setStyle("-fx-background-color: #9DE3F2FF;"+
//                        "-fx-cursor:hand;"+
//                        "-fx-border-color: black;");
                idSelected= e.getId();
            });
            b.setShape(rr);
            b1.setShape(rr);
            grid.add(b,0,n);
            grid.add(b1,1,n);
            n++;
        }
        return grid;
    }

    private void init(ObservableList<Event> model) {
        grid.setVgap(5);
        eventService.setPageSize(4);
        int nrpag=model.size()/4;
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

    private List<Event> getGoing()
    {
        List<Event> list=new ArrayList<>();
        eventService.getEventUsers(idUser).forEach(
                e->{
                        list.add(e);
                }
        );
        return list;
    }

    private List<Event> getEventList(){
        List<Event> list=new ArrayList<>();
        eventService.getAll().forEach(
                e->{
                    list.add(e);
                }
        );

        return list;
    }

    public void backButton(MouseEvent event) {
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
    public void createButton(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("createEvent.fxml"));
        stage = (Stage) create.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventCreate controller=fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }



    private List<String> getEvents()
    {
        List<String> list=new ArrayList<>();
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
                    long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)+1;
                    if(diff<=7) {
                        if (diff==0) {
                            if(firstDate.getTime()==secondDate.getTime())
                                list.add(event.getName() + " is today!");
                            else
                                list.add(event.getName() + " is tomorrow!");
                        }else
                            list.add(event.getName() + " is in " + diff + " days!");
                    }
                }
        );
        return list;
    }

    @Override
    public void update(EventType eventType) {
    }



}
