package com.example.laborator7.repository;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.RequestFriendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.utils.events.request.StatusChange;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class RequestRepository extends AbstractDataBaseRepository <Long,RequestFriendship>{
    CrudRepository<Long, User>repoUser;
    CrudRepository<CreatedTuple<Long,Long>, Friendship>repoFriendship;

    public RequestRepository(String url, String username, String password, String tableName, Validator validator, CrudRepository<Long,User>repoUser,CrudRepository<CreatedTuple<Long,Long>, Friendship>repoFriendship) {
        super(url, username, password, tableName, validator);
        this.repoUser=repoUser;
        this.repoFriendship=repoFriendship;
    }
    @Override
    public RequestFriendship findOne(Long aLong) throws IllegalArgumentException {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  requests where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(aLong));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                Timestamp timestamp = resultSet.getTimestamp("data");

                // Converteste Timestamp la LocalDateTime
                LocalDateTime from = timestamp.toLocalDateTime();
                String statut=resultSet.getString("status");
                Friendship friend = new Friendship(id1,id2);
                friend.setDate(from);
                RequestFriendship r= new RequestFriendship(id1,id2,from);
                r.setId(aLong);
                r.setStatus(statut);
                return r;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Iterable<RequestFriendship> findAll() {
        return null;
    }
    public Iterable<RequestFriendship> findAll(Long id1) {
        Set<RequestFriendship> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests where id2=?");


        ) {
            statement.setLong(1,id1);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                 Long c= resultSet.getLong("id1");
                Long id2=resultSet.getLong("id2");
                Timestamp timestamp = resultSet.getTimestamp("data");
                // Converteste Timestamp la LocalDateTime
                LocalDateTime from = timestamp.toLocalDateTime();
                String status=resultSet.getString("status");
                Friendship friend =new Friendship(c,id2);
                friend.setDate(from);
                RequestFriendship requestFriendship=new RequestFriendship(c,id2,from);
                requestFriendship.setId(id);
                requestFriendship.setStatus(status);
                users.add(requestFriendship);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public RequestFriendship save(RequestFriendship entity) {
        String insertFriendshipStatement = "insert into  requests (id1,id2,data,status) values(?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement insertStatement = connection.prepareStatement(insertFriendshipStatement);) {
            insertStatement.setLong(1, entity.getId1());
            insertStatement.setLong(2, entity.getId2());
            insertStatement.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            insertStatement.setString(4, String.valueOf(entity.getStatus()));
            int response=insertStatement.executeUpdate();
            return response==0?null:entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RequestFriendship findUserPassword(String userName, String password) {
        return null;
    }

    @Override
    public RequestFriendship delete(Long aLong) {
        if(aLong==null){
            throw new IllegalArgumentException("id must be not null!");
        }
        String deleteSQL="delete from requests where id=?";
        try(var connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statementDelete=connection.prepareStatement(deleteSQL);){
            statementDelete.setLong(1,aLong);

            RequestFriendship foundFriendship=findOne(aLong);
            int response=0;
            if(foundFriendship!=null){
                response=statementDelete.executeUpdate();
            }

            return response==0?null:foundFriendship;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public RequestFriendship update(RequestFriendship entity) {
        String updateSQL="update requests set id1=?,id2=?,data=?,status=? where id=?";
        try(
                var connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement =connection.prepareStatement(updateSQL);
        ){
            statement.setLong(1, entity.getId1());
            statement.setLong(2, entity.getId2());
            statement.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            statement.setString(4, String.valueOf(entity.getStatus()));
            statement.setLong(5, entity.getId());
            int response =statement.executeUpdate();
            return response==0?null:entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
    public Page findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<RequestFriendship> findAll(Pageable pageable, Long l) {
        return null;
    }
}
