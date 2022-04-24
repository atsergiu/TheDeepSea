package com.gui.service;

import com.gui.domain.Friendship;
import com.gui.domain.Tuple;
import com.gui.domain.User;
import com.gui.events.EventType;
import com.gui.observer.Observable;
import com.gui.observer.Observer;


import com.gui.repository.database.FriendshipDbRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for Friend Entity
 */

public class FriendshipService implements Observable<EventType> {

    private FriendshipDbRepository repo;
    private UserService userService;

    /**
     * Constructor
     *
     * @param repo        Repository for Friend entity,
     * @param userService User service
     */

    public FriendshipService(FriendshipDbRepository repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
    }


    /**
     * Adds friendship in memory
     *
     * @param friend friendship that will be added
     */
    public Friendship addFriendship(Friendship friend) {
        Tuple<Long, Long> id = friend.getId();
        User user1 = userService.findOne(id.getLeft());
        User user2 = userService.findOne(id.getRight());
        if (user1 == null || user2 == null)
            return null;

        Friendship friend2 = repo.save(friend);
        if (friend2 == null) {
            friend2 = new Friendship(-1l, -1l);
            friend2.setId(new Tuple<Long, Long>(-1L, -1L));
            return friend2;
        }
        return friend2;
    }

    /**
     * Deletes friendship from memory
     */
    public Friendship deleteFriendship(Tuple<Long, Long> id) {
        Friendship friend2 = repo.delete(id);
        notifyObservers(new EventType());
        return friend2;
    }

    /**
     * Get all Friendships
     */

    public List<Friendship> getAll() {
        List<Friendship> fr;
        fr = StreamSupport.stream(repo.findAll().spliterator(), false)
                .toList();
        return fr;
    }

    public Friendship findOne(Tuple<Long, Long> id) {
        return repo.findOne(id);
    }

    public List<Friendship> getAllFriendshipOfAUser(Long id1,String st) {
        List<Friendship> list;
        list = StreamSupport.stream(getAll().spliterator(), false)
                .filter(friendship -> friendship.getU1() == id1 || friendship.getU2() == id1)
                .filter(friendship -> friendship.getStatus().equals(st))
                .collect(Collectors.toList());

        return list;
    }


    public List<User> monthFriends(Long id, Integer month) {
        StringBuilder stringBuilder = new StringBuilder();
        if (month < 10)
            stringBuilder.append("0");
        stringBuilder.append(Integer.toString(month));
        String sMonth = stringBuilder.toString();
        List<User> friendships;
        friendships = StreamSupport
                .stream(getAll().spliterator(), false)
                .filter(friendship -> friendship.getU1() == id || friendship.getU2() == id)
                .filter(friendship -> friendship.getStatus().equals("Accept"))
                .filter(e -> e.getDate().split("-")[1].equals(sMonth))
                .map(e -> {
                    if (e.getU1() == id)
                        return e.getU2();
                    return e.getU1();
                })
                .map(userService::findOne)
                .collect(Collectors.toList());
        return friendships;
    }



    public List<User> showFriendReq(Long id) {
        List<User> list = new ArrayList<>();
        getAll().forEach(e -> {
            if (e.getU2() == id && e.getStatus().equals("Pending"))
                list.add(userService.findOne(e.getU1()));
        });
        return list;
    }

    public Friendship changeStatus(Long id2, Long id1, String status) {
        Tuple<Long, Long> id = new Tuple<>(id1, id2);
        Friendship fr1 = repo.findOne(id);
        if (fr1 == null) return null;
        if (fr1.getU1() == id2) return new Friendship(-1L, -1L);
        fr1.setStatus(status);
        Friendship fr = repo.update(fr1);
        notifyObservers(new EventType());
        if (fr != null) return null;
        else return fr1;
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

    public ArrayList<User> raportFriends(Long id, LocalDate date1, LocalDate date2) {
            List<Friendship> friendships;
            friendships = repo.findOfUser(id);
            ArrayList<User> friendshipsRaport=new ArrayList<>();
            for (Friendship e : friendships) {
                String date=e.getDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateFriendship = LocalDate.parse(date, formatter);
                if(date1.compareTo(dateFriendship)<=0 && date2.compareTo(dateFriendship)>=0)
                    if(e.getU1()==id)
                        friendshipsRaport.add(userService.findOne(e.getU2()));
                    else
                        friendshipsRaport.add(userService.findOne(e.getU1()));
            }
            return friendshipsRaport;
    }
}
