package com.example.laborator7.repository;

import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.PageImplementation;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Comparator.*;

public class MessageDBPagingRepository extends MessageDataBaseRepository implements PagingRepository<Long, Message> {

    public MessageDBPagingRepository(String url, String username, String password, String tableName, Validator<Message> validator, UserDataBaseRepository userRepository) {
        super(url, username, password, tableName, validator, userRepository);
    }
    public Page<Message> findAll(Pageable pageable,Long id) {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement =
                     connection.prepareStatement("select * from messages where from_user=? or to_user=? order by id desc limit ? offset ? ");
        ){
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            statement.setLong(1,id);
            statement.setLong(2,id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
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
            return new PageImplementation<>(pageable, messages.stream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Page<Message> findAllPageable(Pageable pageable, Long id_from, Long id_to) {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement =
                     connection.prepareStatement("select * from messages where  (from_user=? and to_user=?)or (from_user=? and to_user=?) ORDER BY id DESC limit ? offset ? ");
        ){
            statement.setInt(5, pageable.getPageSize());
            statement.setInt(6, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            statement.setLong (1,id_from);
            statement.setLong(2,id_to);
            statement.setLong(3,id_to);
            statement.setLong(4,id_from);

            ResultSet resultSet = statement.executeQuery();
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
            List<Message> myList = new ArrayList<>(messages);

            Collections.sort(myList, comparing(Message::getId).reversed());

            return new PageImplementation<>(pageable, messages.stream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
