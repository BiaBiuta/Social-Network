package com.example.laborator7.repository;



import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.Paginator;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class UserDataBaseRepository extends AbstractDataBaseRepository<Long, User>{


    public UserDataBaseRepository(String url, String username, String password, String tableName, Validator<User> validator) {
        super(url, username, password, tableName, validator);
    }

    @Override
    public User findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  users where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String pass =resultSet.getString("password");
                User u = new User(firstName,lastName,pass);
                u.setId(longID);
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    @Override
    public User findOneName(String  name) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  users where first_name = ? and last_name=?");

        ) {
            statement.setString(1, name.split(" ")[0]);
            statement.setString(2, name.split(" ")[1]);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Long longID = resultSet.getLong("id");
                String pass=resultSet.getString("password");
                User u = new User(firstName,lastName,pass);
                u.setId(longID);
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
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
                String pass=resultSet.getString("password");

              User user=new User(firstName,lastName,pass);
                user.setId(id);
//                user.setFriends(friendsList);
                   users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public User findUserPassword(String pass, String nameUser){
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from  users where first_name = ? and last_name=? and password=?");

        ) {
            statement.setString(1, nameUser.split(" ")[0]);
            statement.setString(2, nameUser.split(" ")[1]);
            statement.setString(3, pass);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                Long longID = resultSet.getLong("id");
                String pass1 =resultSet.getString("password");
                User u = new User(firstName,lastName,pass1);
                u.setId(longID);
                return u;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
    @Override
    public Iterable<User> findAll(Long id) {
        return null;
    }


    @Override
    public User save(User entity) {
        validator.validate(entity);
        String insertSQL= "insert into users(first_name,last_name,password) values(?,?,?)";
        try(var connection=DriverManager.getConnection(url,username,password);
           PreparedStatement statementInsert= connection.prepareStatement(insertSQL);){
            statementInsert.setString(1,entity.getFirstName());
            statementInsert.setString(2,entity.getLastName());
            statementInsert.setString(3,entity.getPassword());
            int response=statementInsert.executeUpdate();
            return response==0?null:entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User delete(Long aLong) {
         if(aLong==null){
             throw new IllegalArgumentException("id must be not null!");
         }
         String deleteSQL="delete from "+tableName+" where id=?";
         try(var connection=DriverManager.getConnection(url,username,password);
             PreparedStatement statementDelete=connection.prepareStatement(deleteSQL);){
             statementDelete.setLong(1,aLong);
             User foundUser=findOne(aLong);
             int response=0;
             if(foundUser!=null){
                 response= statementDelete.executeUpdate();
             }
             return response==0?null:foundUser;
         } catch (SQLException e) {
             throw new RuntimeException(e);
         }
    }

    @Override
    public User update(User entity) {
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
    public Page<User> findAll(Pageable pageable) {
        Paginator<User> paginator = new Paginator<User>(pageable, this.findAll());
        return paginator.paginate();
    }

    @Override
    public Page<User> findAll(Pageable pageable, Long l) {
        return null;
    }
}
