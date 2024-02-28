package com.example.laborator7.repository;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.PageImplementation;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.validator.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDBPagingRepository  extends FriendDataBaseRepository implements PagingRepository<CreatedTuple<Long,Long>, Friendship> {
    public FriendshipDBPagingRepository(String url, String username, String password, String tableName, Validator<Friendship> validator, CrudRepository<Long, User> repoUser) {
        super(url, username, password, tableName, validator, repoUser);
    }
    @Override
    public Page<Friendship> findAll(Pageable pageable,Long id) {
        Set<Friendship> friendshis = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from friendships  where id1=? OR id2=?  limit ? offset ?");

        ){
            statement.setLong(1,id);
            statement.setLong(2,id);
            statement.setInt(3, pageable.getPageSize());
            statement.setInt(4, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
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
            return new PageImplementation<>(pageable,friendshis.stream());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    }


