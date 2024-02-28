package com.example.laborator7.repository;

import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.ReplyMessage;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReplyMessageRepository extends AbstractDataBaseRepository<Long,ReplyMessage> {
    private UserDataBaseRepository userRepository;
    public ReplyMessageRepository(String url, String username, String password, String tableName, Validator<ReplyMessage> validator,UserDataBaseRepository userRepository){
        super(url, username, password, tableName, validator);
        this.userRepository=userRepository;
    }

    @Override
    public ReplyMessage findOne(Long aLong) throws IllegalArgumentException {
        try(Connection connect = DriverManager.getConnection(url, username, password);
            PreparedStatement statement =connect.prepareStatement("select*from reply_message where id_message=?");
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
//                return m;
            }

        }catch(SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Iterable<ReplyMessage> findAll() {
        return null;
    }

    @Override
    public Iterable<ReplyMessage> findAll(Long id) {
        return null;
    }

    @Override
    public ReplyMessage save(ReplyMessage entity) {
        return null;
    }

    @Override
    public ReplyMessage findUserPassword(String userName, String password) {
        return null;
    }

    @Override
    public ReplyMessage delete(Long aLong) {
        return null;
    }

    @Override
    public ReplyMessage update(ReplyMessage entity) {
        return null;
    }

    @Override
    public ArrayList<String> allFriendsPerMonth(User entity, Integer localDateTime) {
        return null;
    }

    @Override
    public User findOneName(String name) {
        return null;
    }

    @Override
    public Page<ReplyMessage> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ReplyMessage> findAll(Pageable pageable, Long l) {
        return null;
    }
}