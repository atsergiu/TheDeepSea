package com.gui.repository.database;

import com.gui.domain.Event;
import com.gui.domain.Group;
import com.gui.domain.User;
import com.gui.domain.validators.Validator;
import com.gui.repository.paging.Page;
import com.gui.repository.paging.Pageable;
import com.gui.repository.paging.Paginator;
import com.gui.repository.paging.PagingRepository;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//public class GroupDbRepository implements Repository<Long, Group> {
public class GroupDbRepository implements PagingRepository<Long, Group> {
    private String url;
    private String username;
    private String password;
    private Validator<Group> validator;

    public GroupDbRepository(String url, String username, String password, Validator<Group> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    public boolean exists(Long id){
        ArrayList<Long> groups=new ArrayList<>();
        String sql = "select * from group_members where idgroup= ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var resultSet=ps.executeQuery();;
            if (!resultSet.isBeforeFirst())
                return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Group findOne(Long id) {

        String sql="select * from groups where id=?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return null;
            resultSet.next();
            String nume=resultSet.getString("name");
            ArrayList<Long> list=new ArrayList<>();
            getGroupsUser(id).forEach(e->{list.add(e);});
            Group gr= new Group(list,nume);
            gr.setId(id);
            return gr;
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

    @Override
    public Iterable<Group> findAll() {
        return null;
    }



    @Override
    public Group save(Group group) {
        String sql = "insert into groups(name) values(?)";
        validator.validate(group);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,group.getName());
            ps.executeUpdate();
            var v = ps.getGeneratedKeys();
            v.next();
            Long id = v.getLong(1);
            String sql2 = "insert into group_members(idgroup,iduser) values(?,?)";
            group.getUsers().forEach(e->{
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
        return group;
    }

    //public ArrayList<Long> getGroupsUser(Long id){
    public Iterable<Long> getGroupsUser(Long id){
        //ArrayList<Long> groups=new ArrayList<>();
        Set<Long> groups=new HashSet<>();
        String sql = "select * from group_members where iduser= ? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            var x=ps.executeQuery();;
            while(x.next())
            {
                groups.add(x.getLong("idgroup"));

            }
           return groups;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;

    }

    public boolean verifyGroup(Long id,Long idgroup){
        ArrayList<Long> groups=new ArrayList<>();
        String sql = "select * from group_members where iduser= ? and idgroup=? ";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1,id);
            ps.setLong(2,idgroup);
            var x=ps.executeQuery();;
            if (!x.isBeforeFirst())
                return false;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
    @Override
    public Group delete(Long id) {
        return null;
    }

    @Override
    public Group update(Group group) {
        return null;
    }

    public ArrayList showMOfGroup(Long id) {
        ArrayList<Long> groups=new ArrayList<>();
        String sql = "select * from group_members where idgroup= ? ";
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

    @Override
    public Page<Long> getGroupsUser(Long aLong, Pageable pageable) {
        Paginator<Long> paginator = new Paginator<Long>(pageable, this.getGroupsUser(aLong));
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
        return null;
    }

}
