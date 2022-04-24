package com.gui.service;

import com.gui.domain.Event;
import com.gui.domain.User;
import com.gui.repository.database.EventDbRepository;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventService {

    private EventDbRepository eventDbRepository;
    private UserService userService;
    public EventService(EventDbRepository eventDbRepository,UserService userService) {
        this.eventDbRepository = eventDbRepository;
        this.userService=userService;
    }



    public List<Long> getNotification(Long id){return eventDbRepository.getAllNotifications(id);}

    public void setNotification(Long idevent,Long id, int val){eventDbRepository.setNot(idevent,id, val);}

    public Iterable<Event> getAll(){return eventDbRepository.findAll();}

    public Event findOne(Long id){return eventDbRepository.findOne(id);}

    public int numbOfEvents(){
        int x=0;
        for(var i:getAll())
            x++;
        return x;
    }

    public Event createEvent(Event event){
        var eventR=eventDbRepository.save(event);
        return eventR;
    }

    public Event addParticipant(Long eventId,Long userId){
        var event =eventDbRepository.addParticipant(eventId,userId);
        return event;
    }

    public ArrayList<User> getParticipants(Long eventId){
        var usersIds= eventDbRepository.getParticipants(eventId);
        ArrayList<User> events=new ArrayList<>();
        for (Long userId : usersIds) {
            events.add(userService.findOne(userId));
        }
        return events;
    }

    public Event removeParticipant(Long eventId,Long userId){
        var event =eventDbRepository.removeParticipant(eventId,userId);
        return event;
    }

    public ArrayList<Event> getEventUsers(Long userID){
        var eventIds= eventDbRepository.getEventUser(userID);
        ArrayList<Event> events=new ArrayList<>();
        for (Long eventId : eventIds) {
            events.add(eventDbRepository.findOne(eventId));
        }
        return events;
    }


    private int page = 0;
    private int size = 1;
    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Set<Event> getNextUsers() {
        this.page++;
        return getUsersOnPage(this.page);
    }

    public Set<Event> getUsersOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Event> studentPage = eventDbRepository.getAllEvents(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

}
