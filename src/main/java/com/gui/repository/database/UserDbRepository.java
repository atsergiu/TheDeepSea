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


public class UserDbRepository implements PagingRepository<Long,User> {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDbRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private List<User> getFriendsListU(User user) {
        List<User> friends = new ArrayList<>();
        String sql = "select us.id, us.first_name, us.last_name, us.gender,us.email,us.password" +
                " from users as us inner join friendships as f on us.id = f.id2 where f.id1 = ? and status='Accepted'" +
                "union select us.id, us.first_name, us.last_name, us.gender,us.email,us.password from users as " +
                "us inner join friendships as f on us.id = f.id1 where f.id2 = ? and status='Accepted'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user.getId());
            statement.setLong(2, user.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long friendId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                User friend = new User(firstName, lastName, gender);
                friend.setId(friendId);
                friends.add(friend);
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Iterable<User> getFriendsList(Long user , String st) {
        Set<User> friends = new HashSet<>();
        String sql = "select us.id, us.first_name, us.last_name, us.gender,us.email,us.password" +
                " from users as us inner join friendships as f on us.id = f.id2 where f.id1 = ? and status=?" +
                "union select us.id, us.first_name, us.last_name, us.gender,us.email,us.password from users as " +
                "us inner join friendships as f on us.id = f.id1 where f.id2 = ? and status=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, user);
            statement.setString(2,st);
            statement.setLong(3, user);
            statement.setString(4,st);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Long friendId = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                User friend = new User(firstName, lastName, gender);
                friend.setId(friendId);
                friends.add(friend);
            }
            return friends;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User findOne(Long id) {
        String sql = "SELECT * from users where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return null;
            resultSet.next();
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String gender = resultSet.getString("gender");
            String email = resultSet.getString("email");
            String passw = resultSet.getString("password");
            User user = new User(firstName, lastName, gender,email,passw);
            user.setId(id);
            user.setFriends(getFriendsListU(user));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                String email = resultSet.getString("email");
                String passw = resultSet.getString("password");
                User user = new User(firstName, lastName, gender,email,passw);
                user.setId(id);
                user.setFriends(getFriendsListU(user));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User save(User entity) {

        String sql = "insert into users (first_name, last_name,gender,email,password ) values (?, ?,?,?,?)";
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getGender());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getPassword());

            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public User delete(Long id) {
        User user = findOne(id);
        if (user == null)
            return null;
        String sql = "delete from users where id= ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User update(User entity) {
        validator.validate(entity);
        String sql = "update users set first_name=?,last_name=?,gender=? where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, entity.getFirstName());
            ps.setString(2, entity.getLastName());
            ps.setString(3, entity.getGender());

            ps.setLong(4, entity.getId());
            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Page<Long> getGroupsUser(Long aLong, Pageable pageable) {
        return null;
    }

    @Override
    public Page<User> getUsers(Pageable pageable) {
        Paginator<User> paginator = new Paginator<User>(pageable, this.findAll());
        return paginator.paginate();
    }


    @Override
    public Page<User> getAllFriends(Long id,Pageable pageable,String st) {
        Paginator<User> paginator = new Paginator<User>(pageable, this.getFriendsList(id,st));
        return paginator.paginate();
    }

    @Override
    public Page<Event> getAllEvents(Pageable pageable) {
        return null;
    }
}
