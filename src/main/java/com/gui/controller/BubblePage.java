package com.gui.controller;

import com.gui.MainGUI;
import com.gui.domain.Message;
import com.gui.domain.User;
import com.gui.domain.validators.GroupValidator;
import com.gui.domain.validators.MessageValidator;
import com.gui.domain.validators.UserValidator;
import com.gui.events.EventType;
import com.gui.observer.Observer;
import com.gui.repository.database.GroupDbRepository;
import com.gui.repository.database.MessageDbRepository;
import com.gui.repository.database.UserDbRepository;
import com.gui.service.MessageService;
import com.gui.service.Page;
import com.gui.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BubblePage implements Observer<EventType> {

    @FXML
    private ImageView back;

    @FXML
    private Label convName;

    @FXML
    private TextArea msg;

    @FXML
    private ImageView send;
    @FXML
    private ListView<Message> messages;
    private Long idUser;
    private Long idGroup;
    private String name;
    private MessageService messageService;
    private UserService userService;
    @FXML
    private ImageView userList;
    private Page page;
    public BubblePage() {

    }

    @FXML
    void back(MouseEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(MainGUI.class.getResource("mainPage.fxml"));
        var stage = (Stage) back.getScene().getWindow();
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainPage controller = fxmlLoader.getController();
        controller.init(page);
        stage.setScene(scene);
    }

    @FXML
    void enterSend(KeyEvent event) {
        //momentan nu merge idk dc sunt trist
        if (event.getCode().equals(KeyCode.ENTER)) {

            sendMsgBasic();
        }
    }

    @FXML
    void sendMsg(MouseEvent event) {
        sendMsgBasic();
    }

    @FXML
    void clearSelect(MouseEvent event){
        int index = messages.getSelectionModel().getSelectedIndex();
        messages.getSelectionModel().clearSelection(index);
    }

    void sendMsgBasic() {
        var message = msg.getText();
        if (message == null || message.equals(""))
            return;
        var reply = messages.getSelectionModel().getSelectedItem();
        if (reply == null)
            messageService.newMessage(idUser, idGroup, message, null);
        else messageService.newMessage(idUser, idGroup, message, reply);
        msg.clear();
    }

    public void init(Page page, Long idGroup) {
        messages.setCellFactory(stringListView -> new ListViewCell(idUser,messageService));
        messages.setStyle("-fx-selection-bar:grey ;");
        this.idUser = page.getIdUser();
        this.idGroup = idGroup;
        this.page=page;
        this.messageService=page.getMessageService();
        this.userService=page.getUserService();
        name=messageService.getGroup(idGroup).getName();
        messageService.addObserver(this);
        convName.setText(name);
        convName.setMinHeight(Region.USE_PREF_SIZE);
        loadMsgStart();
    }
    public void loadMsgStart() {
        var msgs = messageService.getMessages2(idUser, idGroup);
        messages.getItems().setAll(msgs);
    }

    public void loadMsgMeanwhile() {
        var msgs = messageService.getMessages2(idUser, idGroup);
        messages.getItems().setAll(msgs);
    }

    @Override
    public void update(EventType eventType) {
        loadMsgMeanwhile();
    }

    @FXML
    void list(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("blurp blurp");
        String text="The group members in the \" "+name+"\" conversation are: \n";
        String text2="";
        for (User user : this.messageService.showMOfGroup(idGroup)) {
            text2+=user.getFullName();
            text2+="\n";
        }
        alert.setHeaderText(text);
        alert.setContentText(text2);
        alert.showAndWait();
    }
}


final class ListViewCell extends ListCell<Message> {
    private Long idUser;
    private MessageService messageService;
    public ListViewCell(Long userId,MessageService messageService) {
        this.idUser = userId;
        this.messageService=messageService;
    }

    @Override
    protected void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            if (item.getFrom().getId() == idUser) {

                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER_RIGHT);
                Label label = styleLabel(item.getGUIMessage());
                label.setAlignment(Pos.CENTER_RIGHT);
                var reply=item.getReply();
                if(reply==null)
                vBox.getChildren().addAll(label);
                else
                {
                    var user=reply.getFrom();
                    Label textReply=new Label("You replied to: "+user.getFullName());
                    textReply.setAlignment(Pos.CENTER_RIGHT);
                    Label replyLabel=styleReplyLabel(reply.getGUIMessage());
                    replyLabel.setAlignment(Pos.CENTER_RIGHT);
                    vBox.getChildren().addAll(textReply,replyLabel,label);
                }
                setGraphic(vBox);
            }
            else{
                VBox vBox = new VBox();
                vBox.setAlignment(Pos.CENTER_LEFT);
                Label label = styleLabel(item.getGUIMessage());
                label.setAlignment(Pos.CENTER_LEFT);
                var reply=item.getReply();
                if(reply==null)
                    vBox.getChildren().addAll(label);
                else
                {
                    var user=reply.getFrom();
                    Label textReply=new Label("Reply to: "+user.getFullName());
                    textReply.setAlignment(Pos.CENTER_LEFT);
                    Label replyLabel=styleReplyLabel(reply.getGUIMessage());
                    replyLabel.setAlignment(Pos.CENTER_LEFT);
                    vBox.getChildren().addAll(textReply,replyLabel,label);
                }
                setGraphic(vBox);
            }
        }
    }


    private Label styleLabel(String msg){
        var label=new Label(msg);
        label.setMinWidth(50);
        label.setMinHeight(50);
        label.setStyle("-fx-hgap: 10px;" +
                "    -fx-padding: 20px;" +
                "" +
                "    -fx-background-color: #2969c0;" +
                "    -fx-background-radius: 25px;" +
                "" +
                "    -fx-border-radius: 25px;" +
                "    -fx-border-width: 5px;" +
                "    -fx-border-color: black;" +
                "-fx-text-fill: white;" +
                "    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);");
        return label;
    }

    private Label styleReplyLabel(String msg){
        var label=new Label(msg);
        label.setMinWidth(50);
        label.setMinHeight(50);
        label.setStyle("-fx-hgap: 5px;" +
                "    -fx-padding: 5px;" +
                "" +
                "    -fx-background-color: #87a3c9;" +
                "    -fx-background-radius: 13px;" +
                "" +
                "    -fx-border-radius: 13px;" +
                "    -fx-border-width: 5px;" +
                "    -fx-border-color: #272d36;" +
                "-fx-text-fill: black;" +
                "    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.8), 10, 0, 0, 0);");
        return label;
    }
}