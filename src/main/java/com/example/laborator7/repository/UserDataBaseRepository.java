package org.example.repository;


import org.example.domain.User;
import org.example.validator.ValidationException;
import org.example.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserDataBaseRepository extends AbstractDataBaseRepository<Long, User>{


    public UserDataBaseRepository(String url, String username, String password, String tableName, Validator<User> validator) {
        super(url, username, password, tableName, validator);
    }

    @Override
    public Optional<User> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  users where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User u = new User(firstName,lastName);
                u.setId(longID);
                return Optional.ofNullable(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from "+tableName+"");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
//                List<Long> friends= (List<Long>) resultSet.getArray("friends");
//                ArrayList<User> friendsList=new ArrayList<>();
//                if(friends!=null){
//                    for(Long friend:friends){
//                        User userFriend=findOne(friend).get();
//                        friendsList.add(userFriend);
//                    }
//                }
              User user=new User(firstName,lastName);
                user.setId(id);
//                user.setFriends(friendsList);
                   users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<User> save(User entity) {
        validator.validate(entity);
        String insertSQL= "insert into users(first_name,last_name) values(?,?)";
        try(var connection=DriverManager.getConnection(url,username,password);
           PreparedStatement statementInsert= connection.prepareStatement(insertSQL);){
            statementInsert.setString(1,entity.getFirstName());
            statementInsert.setString(2,entity.getLastName());
            int response=statementInsert.executeUpdate();
            return response==0?Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> delete(Long aLong) {
         if(aLong==null){
             throw new IllegalArgumentException("id must be not null!");
         }
         String deleteSQL="delete from "+tableName+" where id=?";
         try(var connection=DriverManager.getConnection(url,username,password);
             PreparedStatement statementDelete=connection.prepareStatement(deleteSQL);){
             statementDelete.setLong(1,aLong);
             Optional<User>foundUser=findOne(aLong);
             int response=0;
             if(foundUser.isPresent()){
                 response= statementDelete.executeUpdate();
             }
             return response==0?Optional.empty():foundUser;
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
    }

    @Override
    public Optional<User> update(User entity) {
        validator.validate(entity);
        String updateSQL="update users set first_name=?,last_name=? where id=?";
        try(
                var connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statement =connection.prepareStatement(updateSQL);
            ){
            statement.setString(1, entity.getFirstName());
            statement.setString(2, entity.getLastName());
            statement.setLong(3,entity.getId());
            int response =statement.executeUpdate();
            return response==0?Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> allFriendsPerMonth(User entity, Integer localDateTime) {
        return null;
    }
}
