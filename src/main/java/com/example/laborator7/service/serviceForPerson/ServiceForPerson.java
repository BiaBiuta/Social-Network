package com.example.laborator7.service.serviceForPerson;


import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.CrudRepository;
import com.example.laborator7.repository.RepositoryWithOptional;
import com.example.laborator7.service.CommunityComponents;
import com.example.laborator7.service.Service;
import com.example.laborator7.validator.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceForPerson implements Service<Long> {
    private final CrudRepository<Long, User> repoUser;
    private final CrudRepository<CreatedTuple<Long,Long>, Friendship> repoFriendship;

    public ServiceForPerson(CrudRepository<Long, User> repoUser, CrudRepository<CreatedTuple<Long, Long>, Friendship> repoFriendship) {
        this.repoUser = repoUser;
        this.repoFriendship = repoFriendship;
    }

    @Override
    public boolean addServiceUser(User user) {
        try{
            repoUser.save(user);
            return true;
        }
        catch (ValidationException | IllegalArgumentException e){
            System.err.println(e);
            return false;
        }
    }

    @Override
    public boolean updateServiceUser(User user) {
        return false;
    }

    @Override
    public Entity<Long> deleteServiceUser(Long id) {
        Entity<Long> result;
        Iterable<Friendship> allFriendship = repoFriendship.findAll();
        List<Friendship> elementsToDelete = new ArrayList<>();

        // Caută și identifică relațiile de prietenie pentru ștergere
        for (Friendship el : allFriendship) {
            if (Objects.equals(el.getId().getLeft(), id) || Objects.equals(el.getId().getRight(), id)) {
                elementsToDelete.add(el); // Adaugă elementele în lista auxiliară pentru ștergere
            }
        }
        elementsToDelete.forEach(el -> {
            if (Objects.equals(el.getId().getLeft(), id)) {
                User friend = repoUser.findOne(el.getId().getRight());
                User userForDelete = repoUser.findOne(id);
                try {
                    repoFriendship.delete(el.getId());//stergem friendshipul dintre cei 2
                } catch (ValidationException | IllegalArgumentException ignored) {

                }
                List<User> userRestForFriend = new ArrayList<User>();
                userRestForFriend = friend.getFriends();
                userRestForFriend.remove(userForDelete);
                friend.setFriends(userRestForFriend);
            }
            if (Objects.equals(el.getId().getRight(), id)) {
                try {
                    repoFriendship.delete(el.getId());//sunt salvati inca o data asa ca sterge
                    List<User> userRestForFriend = new ArrayList<User>();
                    User friend = repoUser.findOne(el.getId().getLeft());
                    User userForDelete = repoUser.findOne(id);
                    userRestForFriend = friend.getFriends();
                    userRestForFriend.remove(userForDelete);
                    friend.setFriends(userRestForFriend);
                } catch (ValidationException | IllegalArgumentException ignored) {

                }


            }
        });
        //System.out.println("2");


        try {
            result = repoUser.delete(id);
        } catch (ValidationException | IllegalArgumentException e) {
            System.err.println(e);
            result = null;
        }
        return result;
    }

    @Override
    public Iterable<User> getAllUser() {
        return repoUser.findAll();
    }

    @Override
    public Friendship deleteServiceFriendship(Long userId1,Long userId2) {

        var ref = new Object() {
            User user1 = null;
            User user2 = null;
        };
        if(repoUser.findOne(userId1)!=null && repoUser.findOne(userId2)!=null) {
            ref.user1 = repoUser.findOne(userId1);
            ref.user2 = repoUser.findOne(userId2);
        }

        CreatedTuple<Long,Long> tuple=new CreatedTuple<>(userId1,userId2);
        Iterable<Friendship>  allFriendship= repoFriendship.findAll();
        List<Friendship> elementsToDelete = new ArrayList<>();

        allFriendship.forEach(elementsToDelete::add);
//        for(Friendship el:elementsToDelete) {
//
//            if ((el.getId().getLeft().equals( tuple.getLeft()) && el.getId().getRight().equals(tuple.getRight())) ||( el.getId().getRight().equals(tuple.getLeft()) &&el.getId().getLeft().equals(tuple.getRight()))) {
//                try {
//                    repoFriendship.delete(el.getId());
//                    List<User> userRestForUser1=new ArrayList<User>();
//                    assert user1 != null;
//                    userRestForUser1=user1.getFriends();
//                    userRestForUser1.remove(user2);
//                    user1.setFriends(userRestForUser1);
//                    List<User> userRestForUser2=new ArrayList<User>();
//                    userRestForUser2=user2.getFriends();
//                    userRestForUser2.remove(user1);
//                    user2.setFriends(userRestForUser2);
//                    return el;
//                }
//                catch(IllegalArgumentException| ValidationException e){
//                    System.err.println(e);
//                }
//            }
//        }
        List<Friendship> collect = StreamSupport.stream(allFriendship.spliterator(), false).filter(el->(el.getId().getLeft().equals( tuple.getLeft()) && el.getId().getRight().equals(tuple.getRight())) ||( el.getId().getRight().equals(tuple.getLeft()) &&el.getId().getLeft().equals(tuple.getRight()))).toList();
        for (Friendship el : collect) {
            try {
                repoFriendship.delete(el.getId());
                List<User> userRestForUser1 = new ArrayList<>();
                assert ref.user1 != null;
                userRestForUser1 = ref.user1.getFriends();
                userRestForUser1.remove(ref.user2);
                ref.user1.setFriends(userRestForUser1);
                List<User> userRestForUser2 = new ArrayList<>();
                userRestForUser2 = ref.user2.getFriends();
                userRestForUser2.remove(ref.user1);
                ref.user2.setFriends(userRestForUser2);
                return el;
            } catch (IllegalArgumentException | ValidationException e) {
                System.err.println(e);
            }
        }
        throw  new IllegalArgumentException("the entity request for deleting does not exist!");
    }

    @Override
    public boolean addServiceFriendship(Friendship friendship) {
        //verific daca mai am prietenia scrisa invers
//        for(Friendship el:repoFriendship.findAll()){
//            if(el.getId().equals(friendship.getId())){
//                throw new IllegalArgumentException("exista deja");
//            }
//        }
        Predicate<Friendship> friendshipPredicate= el-> el.getId().equals(friendship.getId());
        List<Friendship> collect = StreamSupport.stream(repoFriendship.findAll().spliterator(), false).filter(friendshipPredicate).collect(Collectors.toList());
        collect.forEach(el -> {
            throw new IllegalArgumentException("exista deja");
        });
        try {
            repoFriendship.save(friendship);
            long c=friendship.getId().getLeft();
            User optionalUser=repoUser.findOne(friendship.getId().getLeft());
            User user1=null;
            User user2=null;
            if(optionalUser!=null) {
                user1 = repoUser.findOne(friendship.getId().getLeft());
            }
            if(optionalUser==null){
                throw new IllegalArgumentException("e null");
            }
            User optionalUser2=repoUser.findOne(friendship.getId().getRight());
            if(optionalUser2!=null) {
                user2 = repoUser.findOne(friendship.getId().getRight());
            }
            if(optionalUser2==null){
                throw new IllegalArgumentException("e null");
            }
            List<User> friendsForUser1=user1.getFriends();
            List<User> friendsForUser2=user2.getFriends();
            if (friendsForUser1==null){
                friendsForUser1=new ArrayList<>();
            }
            if (friendsForUser2==null){
                friendsForUser2=new ArrayList<>();
            }
            friendsForUser1.add(user2);
            user1.setFriends(friendsForUser1);
            friendsForUser2.add(user1);
            user2.setFriends(friendsForUser2);
            return true;
        }
        catch(IllegalArgumentException|ValidationException v){
            System.err.println(v);
            return false;
        }
    }

    @Override
    public Iterable<Friendship> getAllFriendship() {
        return repoFriendship.findAll();
    }
    @Override
    public int numberOfCommunity(){
        CommunityComponents communityComponents=new CommunityComponents(repoUser);
        return communityComponents.numberOfCommunities();
    }
    @Override
    public ArrayList<User> theLongestComponent(){
        CommunityComponents communityComponents=new CommunityComponents(repoUser);
        return communityComponents.theLongestCommunity();
    }
    @Override
    public ArrayList<User> theLongestComponentWithBfs(){
        CommunityComponents communityComponents=new CommunityComponents(repoUser);
        return communityComponents.theLongestCommunityWithBfs();
    }
    @Override
    public ArrayList<String> allStrings(Long user, Integer local) {
        return null;
    }


}
