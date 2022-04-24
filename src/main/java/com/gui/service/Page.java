package com.gui.service;

import com.gui.domain.Message;
import com.gui.domain.validators.*;
import com.gui.repository.database.*;

public class Page {
    EventService eventService;
    FriendshipService friendshipService;
    MessageService messageService;
    UserService userService;
    Long idUser;
    String firstName;
    String lastName;
    String gender;
    String email;

    public Page(Long idUser) {
        EventDbRepository eventDbRepository=new EventDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new EventValidator());
        UserDbRepository userDbRepository = new UserDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new UserValidator());
        this.messageService = new MessageService(

                new MessageDbRepository("jdbc:postgresql://localhost:5433/socialnetwork",
                        "postgres", "postgres", new MessageValidator()),
                userDbRepository, new GroupDbRepository("jdbc:postgresql://localhost:5433/socialnetwork",

                "postgres", "postgres",
                new GroupValidator()));
        this.idUser = idUser;
        this.userService = new UserService(userDbRepository);
        this.eventService=new EventService(eventDbRepository,userService);
        FriendshipDbRepository friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5433/socialnetwork", "postgres", "postgres", new FriendValidator());
        this.friendshipService = new FriendshipService(friendshipDbRepository, userService);
        var user=userService.findOne(idUser);
        this.firstName=user.getFirstName();
        this.lastName=user.getLastName();
        this.gender=user.getGender();
        this.email=user.getEmail();
    }

    public Long getIdUser() {
        return idUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public EventService getEventService() {
        return eventService;
    }

    public FriendshipService getFriendshipService() {
        return friendshipService;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public UserService getUserService() {
        return userService;
    }
}
