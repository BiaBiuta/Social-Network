package com.example.laborator7.repository;

import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.PageImplementation;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.validator.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class UserDBPagingRepository extends UserDataBaseRepository implements PagingRepository<Long, User>{
    public UserDBPagingRepository(String url, String username, String password, String tableName, Validator<User> validator) {
        super(url, username, password, tableName, validator);
    }
    @Override
    public Page<User> findAll(Pageable pageable,Long id) {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement =
                     connection.prepareStatement("select * from users where id!=? limit ? offset ?");
        ){
            statement.setLong(1,id);
            statement.setInt(2, pageable.getPageSize());
            statement.setInt(3, (pageable.getPageNumber() - 1) * pageable.getPageSize());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
            {
                Long id1= resultSet.getLong("id");
                String firstName=resultSet.getString("first_name");
                String lastName=resultSet.getString("last_name");
                String pass=resultSet.getString("password");
                User user=new User(firstName,lastName,pass);
                user.setId(id1);
                users.add(user);
            }
            return new PageImplementation<>(pageable, users.stream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
