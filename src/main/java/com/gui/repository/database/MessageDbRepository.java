package com.gui.repository.database;

import com.gui.domain.Message;
import com.gui.domain.User;
import com.gui.domain.validators.Validator;
import com.gui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDbRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;
    private Long index;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        index = loadIndex();
    }

    private Long loadIndex() {
        String sql = "SELECT *from messages ORDER BY id DESC";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return 1L;
            resultSet.next();
            return resultSet.getLong("id") + 1L;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private List<User> getFriendsList(User user) {
        List<User> friends = new ArrayList<>();
        String sql = "select us.id, us.first_name, us.last_name, us.gender" +
                " from users as us inner join friendships as f on us.id = f.id2 where f.id1 = ? " +
                "union select us.id, us.first_name, us.last_name, us.gender from users as " +
                "us inner join friendships as f on us.id = f.id1 where f.id2 = ?";
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

    public User findUser(Long id) {
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
            User user = new User(firstName, lastName, gender);
            user.setId(id);
            user.setFriends(getFriendsList(user));
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Set<Long> getGroupsUser(Long id){
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



    public ArrayList<Message> findMessagesFromFriend(Long id,Long idFriend){
        var groupsUser=getGroupsUser(id);
        var groupsFriend=getGroupsUser(idFriend);
        var common=new ArrayList<Long>();
        for (Long aLong : groupsUser) {
            if(groupsFriend.contains(aLong))
                common.add(aLong);
        }
        return findMessagesFromUser(idFriend,common);

    }

    public ArrayList<Message> findMessagesFromUser(Long id,ArrayList<Long> groups){
        ArrayList<Message> result=new ArrayList<>();
        for (Long group : groups) {
            String sql = "select * from messages where tou= ? and fromu=? ";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1,group);
                ps.setLong(2,id);
                var x=ps.executeQuery();;
                while(x.next())
                {
                    Long idmsg = x.getLong("id");
                    Long from = x.getLong("fromu");
                    String message = x.getString("message");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(x.getString("date"), formatter);
                    Message msg = new Message(findUser(from), group, message, dateTime);
                    msg.setId(idmsg);
                    result.add(msg);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public ArrayList<Message> findMessagesToUser(Long id){
        ArrayList<Message> result=new ArrayList<>();
        var groups=getGroupsUser(id);
        for (Long group : groups) {
            String sql = "select * from messages where tou= ? and fromu<>? ";
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1,group);
                ps.setLong(2,id);
                var x=ps.executeQuery();;
                while(x.next())
                {
                    Long idmsg = x.getLong("id");
                    Long from = x.getLong("fromu");
                    String message = x.getString("message");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(x.getString("date"), formatter);
                    Message msg = new Message(findUser(from), group, message, dateTime);
                    msg.setId(idmsg);
                    result.add(msg);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Override
    public Message findOne(Long id) {
        String sql = "SELECT * from messages where id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.isBeforeFirst())
                return null;
            resultSet.next();
            Long to = resultSet.getLong("tou");
            Long from = resultSet.getLong("fromu");
            String message = resultSet.getString("message");
            //String dateTime= resultSet.getString("date");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date"), formatter);
            Message msg = new Message(findUser(from), to, message, dateTime);
            msg.setId(id);
            return msg;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Message> findAll() {
        return null;
    }

    public List<Message> getConversation2(Long id2) {
        List<Message> msgs = new ArrayList<>();
        String sql = "select  * from messages  where tou=? order by date";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id2);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.isBeforeFirst())
                return msgs;
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long to = resultSet.getLong("tou");
                Long from = resultSet.getLong("fromu");
                String message = resultSet.getString("message");
                //String dateTime= resultSet.getString("date");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date"), formatter);
                Message msg = new Message(findUser(from), id2, message, dateTime);
                Long replyId=resultSet.getLong("reply");
                if(replyId!=null)
                    msg.setReply(findOne(replyId));
                else
                    msg.setReply(null);
                msg.setId(id);
                msgs.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msgs;

    }


    public List<String> getConversation(Long id1, Long id2) {
        List<String> convo = new ArrayList<>();
        String sql = "select  * from messages  where (fromu=? and tou=?) or (fromu=? and tou=?) order by id";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id1);
            ps.setLong(2, id2);
            ps.setLong(3, id2);
            ps.setLong(4, id1);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.isBeforeFirst())
                return convo;
            resultSet.next();
            String message = resultSet.getString("message");
            convo.add(message);
            Long idReply = resultSet.getLong("reply");
            while (idReply != 0) {
                sql = "select * from messages  where id=?";
                try (Connection connection2 = DriverManager.getConnection(url, username, password);
                     PreparedStatement ps2 = connection.prepareStatement(sql)) {
                    ps2.setLong(1, idReply);
                    ResultSet resultSet2 = ps2.executeQuery();
                    if (!resultSet2.isBeforeFirst())
                        return null;
                    resultSet2.next();
                    message = resultSet2.getString("message");
                    convo.add(message);
                    idReply = resultSet2.getLong("reply");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return convo;
    }

    //find msg of user
    public List<Message> findMsg(Long id) {
        String sql = "select  * from messages where tou=? and reply is null order by id";
        List<Message> msgs = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if (!resultSet.isBeforeFirst())
                return msgs;
            while (resultSet.next()) {
                Long idmsg = resultSet.getLong("id");
                Long from = resultSet.getLong("fromu");
                String message = resultSet.getString("message");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(resultSet.getString("date"), formatter);
                Message msg = new Message(findUser(from), id, message, dateTime);
                msg.setId(idmsg);
                msgs.add(msg);
            }
            return msgs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message save(Message entity) {
        if (entity.getReply() == null) {
            String sql = "insert into messages (id,fromu,tou,message,date) values (?,?,?,?,?)";
            validator.validate(entity);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                    try {
                        entity.setId(index);
                        ps.setLong(1, index);
                        ps.setLong(2, entity.getFrom().getId());
                        ps.setLong(3, entity.getToGroup());
                        ps.setString(4, entity.getMessage());
                        ps.setString(5, entity.getDate().format(formatter));
                        ps.executeUpdate();
                        index++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return entity;
        } else {
            String sql = "insert into messages (id,fromu,tou,message,date,reply) values (?,?,?,?,?,?)";
            validator.validate(entity);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                    try {
                        entity.setId(index);
                        ps.setLong(1, index);
                        ps.setLong(2, entity.getFrom().getId());
                        ps.setLong(3, entity.getToGroup());
                        ps.setString(4, entity.getMessage());
                        ps.setString(5, entity.getDate().format(formatter));
                        ps.setLong(6, entity.getReply().getId());
                        ps.executeUpdate();
                        index++;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return entity;
        }
    }


    @Override
    public Message delete(Long id) {
        return null;
        //Nu e necesar, cred?(?)?
    }


    public Message messagesOneUserFromOther(Long idUser1,Long idUser2){
        return null;
    }

    @Override
    public Message update(Message entity) {
        validator.validate(entity);
        String sql = "update messages set reply=? where id=? and reply is null";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, entity.getReply().getId());
            ps.setLong(2, entity.getId());
            int nrLines = ps.executeUpdate();
            if (nrLines == 1)
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entity;
    }
}
