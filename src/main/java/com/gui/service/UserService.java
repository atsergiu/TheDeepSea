package com.gui.service;

import com.gui.domain.User;
import com.gui.events.EventType;
import com.gui.observer.Observable;
import com.gui.observer.Observer;
import com.gui.repository.database.UserDbRepository;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for Utilizator Entity
 */
public class UserService implements Observable<EventType> {
    //private UserDbRepository repo;
    private UserDbRepository repo;

    //public UserService(UserDbRepository repo) {
    public UserService(UserDbRepository repo) {
        this.repo = repo;
    }

    public User addUtilizator(User user) {
        User user2 = repo.save(user);
        return user2;
    }

    public User deleteUtilizator(Long id, FriendshipService friendshipService) {
        User user2 = repo.delete(id);
        return user2;
    }

    public Iterable<User> getAll() {
        return repo.findAll();
    }

    public int nrOfUsers()
    {
        int n=0;
        for(var i: getAll())
            n++;
        return n;
    }

    public User findOne(Long id) {
        return repo.findOne(id);
    }

    public User findOneEmail(String email)
    {
       for(var t:getAll())
           if(t.getEmail().equals(email))
               return t;
       return null;
    }

    public User modifyUser(User user) {
        User user1 = repo.update(user);
        return user1;
    }

    private List<User> getFriends(Long id) {
        return findOne(id).getFriends();
    }

    private void findComm(User user, List<Long> visited, ArrayList<User> com) {
        visited.add(user.getId());
        com.add(user);
        getFriends(user.getId()).forEach(friend -> {
            if (!visited.contains(friend.getId()))
                findComm(friend, visited, com);
        });
    }

    private void findAllComms(ArrayList<ArrayList<User>> comms) {
        List<Long> visited = new ArrayList();
        var users = getAll();
        users.forEach(user -> {
            ArrayList<User> com = new ArrayList<>();
            if (!visited.contains(user.getId())) {
                findComm(user, visited, com);
                comms.add(com);
            }
        });
    }

    public ArrayList<User> mostSocialCom() {
        ArrayList<ArrayList<User>> comms = new ArrayList<>();
        findAllComms(comms);

        ArrayList<User> maxCom = null;
        int max = 0;
        for (ArrayList<User> comm : comms)
            if (comm.size() > max) {
                max = comm.size();
                maxCom = comm;
            }
        return maxCom;
    }

    public Integer nrOfCom() {
        ArrayList<ArrayList<User>> comms = new ArrayList<>();
        findAllComms(comms);
        return comms.size();
    }


    private int page = 0;
    private int size = 1;
    private Pageable pageable;

    private int pageF = 0;
    private int sizeF = 1;
    private Pageable pageableF;

    public void setPageSize(int size) {
        this.size = size;
    }

    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }

    public Set<User> getNextUsers() {
        this.page++;
        return getUsersOnPage(this.page);
    }

    public Set<User> getUsersOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = repo.getUsers(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

    public void setPageSizeF(int size) {
        this.sizeF = size;
    }

    public void setPageableF(Pageable pageable) {
        this.pageableF = pageable;
    }


    public Set<User> getNextFriends(Long id,String st) {
        this.pageF++;
        return getFriendsOnPage(id,this.page,st);
    }

    public Set<User> getFriendsOnPage(Long id,int page,String st) {
        this.pageF=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = repo.getAllFriends(id,pageable,st);
        return studentPage.getContent().collect(Collectors.toSet());
    }


    private List<Observer<EventType>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<EventType> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<EventType> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(EventType t) {
        observers.stream().forEach(x->x.update(t));
    }
}


