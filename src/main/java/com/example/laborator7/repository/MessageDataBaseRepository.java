package com.example.laborator7.repository;

import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.RequestFriendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MessageDataBaseRepository extends AbstractDataBaseRepository<Long, Message> {
    public UserDataBaseRepository userRepository;

    public MessageDataBaseRepository(String url, String username, String password, String tableName, Validator<Message> validator, UserDataBaseRepository userRepository) {
        super(url, username, password, tableName, validator);
        this.userRepository = userRepository;
    }

    @Override
    public Message findOne(Long aLong) throws IllegalArgumentException {
        try(Connection connect = DriverManager.getConnection(url, username, password);
            PreparedStatement statement =connect.prepareStatement("select*from messages where id=?");
        ){
            statement.setInt(1, Math.toIntExact(aLong));
            var resultSet=statement.executeQuery();
            if(resultSet.next()){
                Long id=resultSet.getLong("id");
                String fromUser =resultSet.getString("from");
                User from=userRepository.findOneName(fromUser);
                String toStringArray = resultSet.getString("to");
                String[] toArray = toStringArray.split(",");
                String name=null;
                List<User> toU=new ArrayList<>();

                for (String to : toArray) {
                    String[] words = to.split(" ");
                    if (words.length == 2) {
                        String firstWord = words[0];
                        String secondWord = words[1];
                        name = firstWord + " " + secondWord;
                    }

                    User u = findOneName(to);
                    toU.add(u);
                }
                Timestamp timestamp = resultSet.getTimestamp("data");

                // Converteste Timestamp la LocalDateTime
                LocalDateTime fr = timestamp.toLocalDateTime();
                String message=resultSet.getString("message");
                Message m=new Message(from,toU,fr,message);
                m.setDate(fr);
                m.setId(id);
                return m;
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }


    @Override
    public Iterable<Message> findAll() {
        return null;
    }


    @Override
    public Iterable<Message> findAll(Long id) {
        Set<Message> messages = new HashSet<>();
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("select* from messages where from_user=? or to_user=?");
            ){
            statementFind.setLong(1,id);
            statementFind.setLong(2,id);
            ResultSet resultSet=statementFind.executeQuery();
            while(resultSet.next()){
                Long id1=resultSet.getLong("id");
                Long from_user=resultSet.getLong("from_user");
                User from=userRepository.findOne(from_user);
                Long toSet=resultSet.getLong("to_user");
                User to=userRepository.findOne(toSet);
                List<User> toU=new ArrayList<>();
                toU.add(to);
                String text_message= resultSet.getString("text_message");
                Timestamp timestamp = resultSet.getTimestamp("data_message");

                // Converteste Timestamp la LocalDateTime
                LocalDateTime fr = timestamp.toLocalDateTime();
                Message m=new Message(from,toU,fr,text_message);
                m.setId(id1);
                m.setDate(fr);
                messages.add(m);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return messages;
    }
    public Long findTo(Long id){
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("select to_user from messages where id=?");
            ){
            statementFind.setLong(1,id);
            ResultSet resultSet=statementFind.executeQuery();
            Long id_to=0L;
            if(resultSet.next()) {
                 id_to = resultSet.getLong("to_user");

            }
            return id_to;
            }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }


    public Iterable<Message> findAll(Long id_from,Long id_to) {

        Set<Message> messages = new HashSet<>();
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("select* from messages where (from_user=? and to_user=?)or (from_user=? and to_user=?)");
            ) {

            statementFind.setLong (1,id_from);
            statementFind.setLong(2,id_to);
            statementFind.setLong(3,id_to);
            statementFind.setLong(4,id_from);
            ResultSet resultSet=statementFind.executeQuery();
            while (resultSet.next())
            {
                Long id=resultSet.getLong("id");
                List<User> toU=new ArrayList<>();
                Long from=resultSet.getLong("from_user");
                Long to=resultSet.getLong("to_user");
                Timestamp timestamp = resultSet.getTimestamp("data_message");
                LocalDateTime fr = timestamp.toLocalDateTime();
                String text_message=resultSet.getString("text_message");
                Long replied_message= resultSet.getLong("replied_message_id");
                Message message;
                if(from==id_from && to==id_to){
                    User u11=userRepository.findOne(id_from);
                    User u22=userRepository.findOne(id_to);
                    toU.add(u22);
                    message=new Message(u11,toU,fr,text_message);
                    message.setId(id);
                    message.setDate(fr);
                    messages.add(message);
                }
                else{
                    User u11=userRepository.findOne(id_to);
                    User u22=userRepository.findOne(id_from);
                    toU.add(u22);
                    message=new Message(u11,toU,fr,text_message);
                    message.setId(id);
                    message.setDate(fr);
                    messages.add(message);
                }

            }
            return messages;

        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Iterable<Message> findAllMessageFromFriendToFriend(String fromString,String toString) {
        Set<Message> messages = new HashSet<>();
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("select* from messages where receiver=? and sender=?");

            ){
            statementFind.setString(1,fromString);
            statementFind.setString(2,toString);
            ResultSet resultSet=statementFind.executeQuery();
            while(resultSet.next()){
                Long id=resultSet.getLong("id_mess");
                User from=userRepository.findOneName(fromString);
                String message=resultSet.getString("message");
                String toStringArray = resultSet.getString("sender");
                String[] toArray = toStringArray.split(",");
                String name=null;
                List<User> toU=new ArrayList<>();

                for (String to : toArray) {
                    String[] words = to.split(" ");
                    if (words.length == 2) {
                        String firstWord = words[0];
                        String secondWord = words[1];
                        name = firstWord + " " + secondWord;
                    }

                    User u = findOneName(to);
                    toU.add(u);
                }
                Timestamp timestamp = resultSet.getTimestamp("data");

                // Converteste Timestamp la LocalDateTime
                LocalDateTime fr= timestamp.toLocalDateTime();
                Message m=new Message(from,toU,fr,message);
                m.setId(id);
                m.setDate(fr);
                messages.add(m);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return messages;
    }
    @Override
    public Message save(Message entity) {
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("insert into messages(from_user,to_user,,data,message) values(?,?,?,?)");){
            String name=entity.getFrom().getFirstName()+" "+entity.getFrom().getLastName();
            statementFind.setString(1,name);
            //String s=entity.getToString();
            //statementFind.setString(2,s);
            statementFind.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            String message=entity.getMessage();
            statementFind.setString(4,message);
            int response=statementFind.executeUpdate();
            return response==0?null:entity;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message findUserPassword(String userName, String password) {
        return null;
    }

    public Message saveM(Message entity,User a,Long replied_message) {
        try(Connection connected =DriverManager.getConnection(url,username,password);
            PreparedStatement statementFind=connected.prepareStatement("insert into messages(from_user,to_user,text_message,data_message,replied_message_id) values(?,?,?,?,?)");){
           Long from_user =entity.getFrom().getId();
           Long to_user=a.getId();
           String text_message= entity.getMessage();
           statementFind.setLong(1,from_user);
           statementFind.setLong(2,to_user);
           statementFind.setString(3,text_message);
           statementFind.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));
           statementFind.setLong(5,replied_message);

            int response=statementFind.executeUpdate();
            return response==0?null:entity;
        }
        catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message delete(Long aLong) {
        try(Connection connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement=connection.prepareStatement("delete from messages where id=?");){
            statement.setInt(1,Math.toIntExact(aLong));
            Message m=findOne(aLong);
            int response=statement.executeUpdate();
            return response==0?null:m;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Message update(Message entity) {
        return null;
    }

//    @Override
//    public Message update(Message entity) {
//        try(Connection connection=DriverManager.getConnection(url,username,password);
//            PreparedStatement statement=connection.prepareStatement("update messages set from=?,to=?,date=?,message=? where id=?");){
//            statement.setString(1,entity.getFrom().getFirstName()+" "+entity.getFrom().getLastName());
//            statement.setString(2,entity.getTo().getFirstName()+" "+entity.getTo().getLastName());
//            statement.setDate(3,Date.valueOf(String.valueOf(entity.getDate())));
//            statement.setString(4,entity.getMessage());
//            statement.setInt(5,Math.toIntExact(entity.getId()));
//            int response=statement.executeUpdate();
//            return response==0?null:entity;
//        }catch(SQLException e){
//            throw new RuntimeException(e);
//        }
//    }

    @Override
    public ArrayList<String> allFriendsPerMonth(User entity, Integer localDateTime) {
        return null;
    }

    @Override
    public User findOneName(String name) {
        return null;
    }


    @Override
    public Page<Message> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Message> findAll(Pageable pageable, Long l) {
        return null;
    }
}

