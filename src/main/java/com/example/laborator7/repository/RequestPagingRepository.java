package com.example.laborator7.repository;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.RequestFriendship;
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

public class RequestPagingRepository extends RequestRepository implements PagingRepository<Long, RequestFriendship> {
    public RequestPagingRepository(String url, String username, String password, String tableName, Validator validator, CrudRepository<Long, User> repoUser, CrudRepository<CreatedTuple<Long, Long>, Friendship> repoFriendship) {
        super(url, username, password, tableName, validator, repoUser, repoFriendship);
    }
    public Page<RequestFriendship> findAll(Pageable pageable,Long id1) {
        Set<RequestFriendship> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from requests where id2=? limit ? offset ?");


        ) {
            statement.setLong(1,id1);
            statement.setInt(2, pageable.getPageSize());
            statement.setInt(3, (pageable.getPageNumber() - 1) * pageable.getPageSize());
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
            return new PageImplementation<>(pageable, users.stream());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

