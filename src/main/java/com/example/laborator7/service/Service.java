package org.example.service;

import org.example.domain.CreatedTuple;
import org.example.domain.Entity;
import org.example.domain.Friendship;
import org.example.domain.User;
import org.example.validator.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public interface Service<ID>{
    //User
    /**
     *
     * @param user  -user must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if user is null.
     *@throws ValidationException
     *   if user is not valid.
     */
    boolean addServiceUser(User user);
    /**
     *
     * @param id
     *         id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity does not exist.
     *             if the friendship does not delete with success(is necessary
     *             to delete a friendship is our user is connected of someone else)
     */
    Entity<ID> deleteServiceUser(ID id);
    /**
     *
     * @return all entities
     */
    Iterable<User> getAllUser();
    //Friendship
    /**
     *
     * @param userId1
     *         id must be not null
     * @param userId2
     *          id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws ValidationException
     *            if the userId1=userId2
     * @throws IllegalArgumentException
     *             if the given entities does not exist or one of them does not exist .
     *             if the friendship does not delete with success
     */
    Entity<CreatedTuple<ID,ID>> deleteServiceFriendship(Long userId1, Long userId2);
    /**
     *
     * @param friendship
     *         friendship must be not null
     * @return true if it works ,false if iti doesn't
     * @throws ValidationException
     *            at the save function for the identity friendship
     * @throws IllegalArgumentException
     *             if the given friendship  exist
     *             and the IllegalArgumentException of the save repository method
     */
    boolean addServiceFriendship(Friendship friendship);
    /**
     *
     * @return all entities
     */
    Iterable<Friendship> getAllFriendship();
    /**
     *
     * @return number of conex component
     *
     */
    int numberOfCommunity();
    /**
     *
     * @return an  array the community with the most member
     *  it does a dfs method for each connected components and find the largest path
     *
     */
    ArrayList<User> theLongestComponent();
    /**
     *
     * @return an  array of the longest path in communities
     *  it does a bfs method for each connected components and find the longest path
     *
     */
    public ArrayList<User> theLongestComponentWithBfs();
    public ArrayList<String>allStrings(Long user, Integer localDateTime);
}
