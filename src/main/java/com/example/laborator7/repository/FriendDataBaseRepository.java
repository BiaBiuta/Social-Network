package com.example.laborator7.repository;


import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.Paginator;
import com.example.laborator7.validator.Validator;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.*;
public class FriendDataBaseRepository  extends AbstractDataBaseRepository<CreatedTuple<Long,Long>, Friendship>{
    CrudRepository<Long, User>repoUser;
    public FriendDataBaseRepository(String url, String username, String password, String tableName, Validator<Friendship> validator, CrudRepository<Long,User>repoUser) {
        super(url, username, password, tableName, validator);
        this.repoUser=repoUser;
    }


    @Override
    public Friendship findOne(CreatedTuple<Long, Long> longLongCreatedTuple) {
        Friendship friendship=null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from " + tableName + " where id1 = ? and id2=?" + ";");
        ) {
            statement.setLong(1,longLongCreatedTuple.getLeft());
            statement.setLong(2,longLongCreatedTuple.getRight());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                friendship = new Friendship(id1, id2);
                friendship.setId(longLongCreatedTuple);
                Timestamp timestamp = resultSet.getTimestamp("data");

                // Converteste Timestamp la LocalDateTime
                LocalDateTime from = timestamp.toLocalDateTime();

                User user1 = repoUser.findOne(id1);
                User user2 = repoUser.findOne(id2);
                friendship=new Friendship(id1,id2);
                friendship.setDate(from);
                return friendship;
            }
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

        @Override
    public Iterable<Friendship> findAll() {
            Set<Friendship> friendshis = new HashSet<>();

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement statement = connection.prepareStatement("select * from "+tableName);
                 ResultSet resultSet = statement.executeQuery()
            ){
                while(resultSet.next()){
                    Long id1= resultSet.getLong("id1");
                    Long id2= resultSet.getLong("id2");
                    Timestamp timestamp = resultSet.getTimestamp("data");

                    // Converteste Timestamp la LocalDateTime
                    LocalDateTime from = timestamp.toLocalDateTime();
                    Friendship friendship=new Friendship(id1,id2);
                    friendship.setDate(from);
                    friendship.setId(new CreatedTuple<>(id1,id2));
                    friendshis.add(friendship);

                }
                return friendshis;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    @Override
    public Iterable<Friendship> findAll(Long id) {
        Set<Friendship> friendshis = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships  where id1=? OR id2=? ");

        ){  statement.setLong(1,id);
            statement.setLong(2,id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
            Long id1= resultSet.getLong("id1");
            Long id2= resultSet.getLong("id2");
            //Long ID=resultSet.getLong("id");
            Timestamp timestamp = resultSet.getTimestamp("data");

            // Converteste Timestamp la LocalDateTime
            LocalDateTime from = timestamp.toLocalDateTime();
            Friendship friendship=new Friendship(id1,id2);

            friendship.setDate(from);
            friendship.setId(new CreatedTuple<>(id1,id2));
            friendshis.add(friendship);

        }
        return friendshis;
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    public Friendship save(Friendship entity) {
        //validator.validate(entity);
        String insertFriendshipStatement = "insert into " + tableName + " (id1,id2,data) values(?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement insertStatement = connection.prepareStatement(insertFriendshipStatement);) {
            insertStatement.setLong(1, entity.getId().getLeft());
            insertStatement.setLong(2, entity.getId().getRight());
            insertStatement.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            int response=insertStatement.executeUpdate();
            User user1=repoUser.findOne(entity.getId().getLeft());
            User user2=repoUser.findOne(entity.getId().getRight());
            return response==0?null:entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Friendship findUserPassword(String userName, String password) {
        return null;
    }

    @Override
    public Friendship delete(CreatedTuple<Long, Long> longLongCreatedTuple) {
        if(longLongCreatedTuple==null){
            throw new IllegalArgumentException("id must be not null!");
        }
        String deleteSQL="delete from "+tableName+" where id1=? and id2=?";
        try(var connection=DriverManager.getConnection(url,username,password);
            PreparedStatement statementDelete=connection.prepareStatement(deleteSQL);){
            statementDelete.setLong(1,longLongCreatedTuple.getLeft());
            statementDelete.setLong(2,longLongCreatedTuple.getRight());
            Friendship foundFriendship=findOne(longLongCreatedTuple);
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
    public Friendship update(Friendship entity) {
        validator.validate(entity);
        String updateSQL="update "+tableName+" set date=? where id1=? and id2=?";
        try(
                var connection=DriverManager.getConnection(url,username,password);
                PreparedStatement statement =connection.prepareStatement(updateSQL);
        ){
            statement.setDate(1, Date.valueOf(entity.getDate().toLocalDate()));
            statement.setString(2,entity.getId().getLeft().toString());
            statement.setString(3,entity.getId().getRight().toString());
            int response=statement.executeUpdate();
            return response==0?null:entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ArrayList<String> allFriendsPerMonth(User user, Integer datet){
        List<String> friendship=new ArrayList<>();
        String query = "SELECT u2.first_name AS name_friends, u2.last_name AS last_name_friends, f.data " +
                "FROM friendships f " +
                "JOIN users u1 ON f.id1 = u1.id " +
                "JOIN users u2 ON f.id2 = u2.id " +
                "WHERE (f.id1 = ? OR f.id2 = ?) " +
                "AND TO_CHAR(f.data, 'MM') = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = connection.prepareStatement(query);
            ) {
            ArrayList<String>strings=new ArrayList<>();
                statement.setLong(1, user.getId());
                statement.setLong(2, user.getId());
                statement.setString(3, String.valueOf(datet));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String firstName = resultSet.getString("name_friends");
                    String lastName = resultSet.getString("last_name_friends");
                    Timestamp timestamp = resultSet.getTimestamp("data");
                    LocalDateTime from = timestamp.toLocalDateTime();
                    String string= new StringJoiner(" | ")
                            .add( firstName )
                            .add(lastName )
                            .add(String.valueOf(from))
                            .toString();
                    strings.add(string);
                }
             return strings;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

    }

    @Override
    public User findOneName(String name) {
        return null;
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable) {
        Paginator<Friendship> paginator = new Paginator<Friendship>(pageable, this.findAll());
        return paginator.paginate();
    }

    @Override
    public Page<Friendship> findAll(Pageable pageable, Long l) {
        return null;
    }
}
