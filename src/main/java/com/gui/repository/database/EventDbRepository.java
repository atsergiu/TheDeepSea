package com.gui.repository.database;

import com.gui.domain.Event;
import com.gui.domain.User;
import com.gui.domain.validators.Validator;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.Paginator;
import com.gui.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventDbRepository implements PagingRepository<Long, Event> {
    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;

    public EventDbRepository(String url, String username, String password, Validator<Event> validator)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public void setNot(Long idevent, Long id, int val)
    {
        String sql = "update event_members set notification=? where iduser=?  and idevent=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, val);
            ps.setLong(2, id);
            ps.setLong(3, idevent);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Event findOne(Long id) {

        String sql="select * from events where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return null;
            resultSet.next();
            Long creator=resultSet.getLong("creator");
            String nume=resultSet.getString("name");
            String description=resultSet.getString("description");
            String location=resultSet.getString("location");
            String date=resultSet.getString("date");
            ArrayList<Long> list=new ArrayList<>();
            showMOfEvent(id).forEach(e->{list.add(e);});
            Event ev= new Event(creator,nume,description,location,date,list);
            ev.setId(id);
            return ev;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

//        String sql = "select * from groups where id= ? ";
//        try (Connection connection = DriverManager.getConnection(url, username, password);
//             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//            ps.setLong(1,id);
//            var resultSet=ps.executeQuery();
//            resultSet.next();
//            return new Group(null,resultSet.getString("name"));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    return null;

    }
    public ArrayList<Long> showMOfEvent(Long id) {
        ArrayList<Long> groups=new ArrayList<>();
        String sql = "select * from event_members where idevent= ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var x=ps.executeQuery();;
            while(x.next())
            {
                groups.add(x.getLong("iduser"));
            }
            return groups;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    public List<Long> getAllNotifications(Long id)
    {
        List<Long> list=new ArrayList<>();
        String sql = "select * from event_members where iduser= ? and  notification=1 ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var x=ps.executeQuery();;
            while(x.next())
            {
                list.add(x.getLong("idevent"));
            }
            return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Iterable<Event> findAll() {
        Set<Event> eventList = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id=resultSet.getLong("id");
                Long creator=resultSet.getLong("creator");
                String nume=resultSet.getString("name");
                String description=resultSet.getString("description");
                String location=resultSet.getString("location");
                String date=resultSet.getString("date");
                ArrayList<Long> list=new ArrayList<>();
                showMOfEvent(id).forEach(e->{list.add(e);});
                Event ev= new Event(creator,nume,description,location,date,list);
                ev.setId(id);
                eventList.add(ev);
            }
            return eventList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }


    @Override
    public Event save(Event event) {
        String sql = "insert into events(creator,name,description,location,date) values(?,?,?,?,?)";
        validator.validate(event);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,event.getCreator());
            ps.setString(2,event.getName());
            ps.setString(3,event.getDescription());
            ps.setString(4,event.getLocation());
            ps.setString(5,event.getDate());

            ps.executeUpdate();
            var v = ps.getGeneratedKeys();
            v.next();
            Long id = v.getLong(1);
            String sql2 = "insert into event_members(idevent,iduser) values(?,?)";
            event.getParticipants().forEach(e->{
                try {
                    PreparedStatement ps2 = connection.prepareStatement(sql2);
                    ps2.setLong(1,id);
                    ps2.setLong(2,e);
                    ps2.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }


    public Event addParticipant(Long idEvent,Long idUser) {
        String sql = "insert into event_members (idevent,iduser) values (?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, idEvent);
            ps.setLong(2, idUser);
            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Event(null,null,null,null,null,null);
    }

    public Event removeParticipant(Long idEvent,Long idUser) {
        String sql = "delete from event_members where idevent=? and iduser=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, idEvent);
            ps.setLong(2, idUser);
            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Event(null,null,null,null,null,null);
    }


    public ArrayList<Long> getParticipants(Long id){
        ArrayList<Long> participants=new ArrayList<>();
        String sql = "select * from event_members where idevent= ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var x=ps.executeQuery();;
            while(x.next())
            {
                participants.add(x.getLong("iduser"));

            }
            return participants;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    //public ArrayList<Long> getGroupsUser(Long id){
    public ArrayList<Long> getEventUser(Long id){
        //ArrayList<Long> groups=new ArrayList<>();
        ArrayList<Long> events=new ArrayList<>();
        String sql = "select * from event_members where iduser= ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var x=ps.executeQuery();;
            while(x.next())
            {
                events.add(x.getLong("idevent"));

            }
            return events;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;

    }


    @Override
    public Event delete(Long id) {
        return null;
    }

    @Override
    public Event update(Event group) {
        return null;
    }


    @Override
    public Page<Long> getGroupsUser(Long aLong, Pageable pageable) {
        Paginator<Long> paginator = new Paginator<Long>(pageable, this.getEventUser(aLong));
        return paginator.paginate();
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> getAllFriends(Long aLong, Pageable pageable,String st) {
        return null;
    }

    @Override
    public Page<Event> getAllEvents(Pageable pageable) {
        Paginator<Event> paginator = new Paginator<Event>(pageable, this.findAll());
        return paginator.paginate();
    }


}
