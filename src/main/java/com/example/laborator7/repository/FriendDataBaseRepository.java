package org.example.repository;

import org.example.domain.CreatedTuple;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.validator.Validator;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.*;

public class FriendDataBaseRepository  extends AbstractDataBaseRepository<CreatedTuple<Long,Long>, Friendship>{
    RepositoryWithOptional<Long,User>repoUser;
    public FriendDataBaseRepository(String url, String username, String password, String tableName, Validator<Friendship> validator,RepositoryWithOptional<Long,User>repoUser) {
        super(url, username, password, tableName, validator);
        this.repoUser=repoUser;
    }


    @Override
    public Optional<Friendship> findOne(CreatedTuple<Long, Long> longLongCreatedTuple) {
         Friendship friendship=null;
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from " + tableName + " where id = " + longLongCreatedTuple.toString() + ";");
             ResultSet resultSet = statement.executeQuery();) {
            if (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                friendship = new Friendship(id1, id2);
                friendship.setId(longLongCreatedTuple);
                LocalDateTime from = LocalDateTime.parse(resultSet.getString("date"));
                User user1 = repoUser.findOne(id1).get();
                User user2 = repoUser.findOne(id2).get();
                friendship=new Friendship(id1,id2);
                friendship.setDate(from);
                return Optional.of(friendship);
            }
        } catch(SQLException e){
                throw new RuntimeException(e);
        }
        return Optional.empty();
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
    public Optional<Friendship> save(Friendship entity) {
        validator.validate(entity);
        String insertFriendshipStatement = "insert into " + tableName + " (id1,id2,data) values(?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement insertStatement = connection.prepareStatement(insertFriendshipStatement);) {
            insertStatement.setLong(1, entity.getId().getLeft());
            insertStatement.setLong(2, entity.getId().getRight());
            insertStatement.setDate(3, Date.valueOf(entity.getDate().toLocalDate()));
            int response=insertStatement.executeUpdate();
            User user1=repoUser.findOne(entity.getId().getLeft()).get();
            User user2=repoUser.findOne(entity.getId().getRight()).get();
//            String user1Name=user1.getFirstName()+" "+user1.getLastName();
//            String user2Name=user2.getFirstName()+" "+user2.getLastName();
//            PreparedStatement updateStatement=connection.prepareStatement("UPDATE users SET friends = friends || to_jsonb(ARRAY[?]) WHERE id = ?");
//            PreparedStatement updateStatement2 = connection.prepareStatement("UPDATE users SET friends = friends || to_jsonb(ARRAY[?]) WHERE id = ?");
//            updateStatement.setLong(1,entity.getId().getRight());
//            updateStatement.setLong(2,entity.getId().getLeft());
//            updateStatement.executeUpdate();
//            updateStatement2.setLong(1,entity.getId().getLeft());
//            updateStatement2.setLong(2,entity.getId().getRight());
//            updateStatement2.executeUpdate();
            return response==0?Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Friendship> delete(CreatedTuple<Long, Long> longLongCreatedTuple) {
        if(longLongCreatedTuple==null){
            throw new IllegalArgumentException("id must be not null!");
        }
        String deleteSQL="delete from "+tableName+" where id1=? and id2=?";
        try(var connection=DriverManager.getConnection(url,username,password);
        PreparedStatement statementDelete=connection.prepareStatement(deleteSQL);){
            statementDelete.setLong(1,longLongCreatedTuple.getLeft());
            statementDelete.setLong(2,longLongCreatedTuple.getRight());
            Optional<Friendship>foundFriendship=findOne(longLongCreatedTuple);
            int response=0;
            if(foundFriendship.isEmpty()){
                response=statementDelete.executeUpdate();
            }

            return response==0?Optional.empty():foundFriendship;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Optional<Friendship> update(Friendship entity) {
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
            return response==0?Optional.empty():Optional.of(entity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public ArrayList<String>allFriendsPerMonth(User user,Integer datet){
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
}
