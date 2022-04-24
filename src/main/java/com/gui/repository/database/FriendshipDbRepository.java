package com.gui.repository.database;

import com.gui.domain.Event;
import com.gui.domain.Friendship;
import com.gui.domain.Tuple;
import com.gui.domain.User;
import com.gui.domain.validators.Validator;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.PagingRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//public class FriendshipDbRepository implements Repository<Tuple<Long, Long>, Friendship> {
public class FriendshipDbRepository implements PagingRepository<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;

    public FriendshipDbRepository(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> id) {
        String sql = "SELECT * from friendships where ((id1 = ? and id2=?) or (id1=? and id2=?))";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id.getLeft());
            statement.setLong(2, id.getRight());
            statement.setLong(3, id.getRight());
            statement.setLong(4, id.getLeft());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return null;
            resultSet.next();
            Long id1 = resultSet.getLong("id1");
            Long id2 = resultSet.getLong("id2");
            String date = resultSet.getString("date");
            String status = resultSet.getString("status");
            Friendship friendship = new Friendship(id1, id2, date, status);
            friendship.setId(id);
            return friendship;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");

             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");
                String status = resultSet.getString("status");
                Friendship friendship = new Friendship(id1, id2, date, status);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public ArrayList<Friendship> findOfUser(Long id){
        ArrayList<Friendship> friendships = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships where id1=? or id2=?");) {
            statement.setLong(1, id);
            statement.setLong(2, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date = resultSet.getString("date");
                String status = resultSet.getString("status");
                Friendship friendship = new Friendship(id1, id2, date, status);
                friendships.add(friendship);
            }
            return friendships;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }


    @Override
    public Friendship save(Friendship friendship) {

        String sql = "insert into friendships (id1, id2,date,status ) values (?, ?,?,?)";
        validator.validate(friendship);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, friendship.getU1());
            ps.setLong(2, friendship.getU2());
            ps.setString(3, friendship.getDate());
            ps.setString(4, friendship.getStatus());
            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }




    @Override
    public Friendship delete(Tuple<Long, Long> id) {
        Friendship friendship = findOne(id);
        if (friendship == null)
            return null;
        String sql = "delete from friendships where (id1= ? and id2=?) or (id1= ? and id2=?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id.getLeft());
            ps.setLong(2, id.getRight());
            ps.setLong(3, id.getRight());
            ps.setLong(4, id.getLeft());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Friendship update(Friendship friendship) {
        String sql = "update friendships set status=? where id1=? and id2=? and status='Pending'";
        validator.validate(friendship);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, friendship.getStatus());
            ps.setLong(2, friendship.getU1());
            ps.setLong(3, friendship.getU2());

            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendship;
    }



    @Override
    public Page<Long> getGroupsUser(Tuple<Long, Long> longLongTuple, Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> getAllFriends(Tuple<Long, Long> longLongTuple, Pageable pageable,String st) {
        return null;
    }

    @Override
    public Page<Event> getAllEvents(Pageable pageable) {
        return null;
    }


}
