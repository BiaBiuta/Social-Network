package com.example.laborator7.service.serviceForPerson;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;

import com.example.laborator7.repository.FriendshipDBPagingRepository;
import com.example.laborator7.repository.UserDataBaseRepository;

import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PageableImplementation;
import com.example.laborator7.utils.events.friendship.ChangeEventTypeF;
import com.example.laborator7.utils.events.friendship.FriendTaskChangeEvent;
import com.example.laborator7.utils.events.user.ChangeEventType;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceDbForFriendship implements Observable<FriendTaskChangeEvent> {
    private UserDataBaseRepository repo;
    private FriendshipDBPagingRepository repoFriendship;
    private List<Observer<FriendTaskChangeEvent>> observers=new ArrayList<>();
    private int page = 0;
    private int size = 1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private Pageable pageable;


    public ServiceDbForFriendship(UserDataBaseRepository repo, FriendshipDBPagingRepository repoFriendship) {
        this.repo = repo;
        this.repoFriendship = repoFriendship;
    }

    @Override
    public void addObserver(Observer<FriendTaskChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendTaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
    public Friendship deleteServiceUser(Long id1,Long id2) {
        var ref = new Object() {
            User user1 = null;
            User user2 = null;
        };
        if(repo.findOne(id1)!=null && repo.findOne(id2)!=null) {
            ref.user1 = repo.findOne(id1);
            ref.user2 = repo.findOne(id2);
        }

        CreatedTuple<Long,Long> tuple=new CreatedTuple<>(id1,id2);
        Iterable<Friendship>  allFriendship= repoFriendship.findAll();
        List<Friendship> elementsToDelete = new ArrayList<>();

        allFriendship.forEach(elementsToDelete::add);
        List<Friendship> collect = StreamSupport.stream(allFriendship.spliterator(), false).filter(el->(el.getId().getLeft().equals( tuple.getLeft()) && el.getId().getRight().equals(tuple.getRight())) ||( el.getId().getRight().equals(tuple.getLeft()) &&el.getId().getLeft().equals(tuple.getRight()))).toList();
        for (Friendship el : collect) {
            try {
                repoFriendship.delete(el.getId());
                notifyObservers(new FriendTaskChangeEvent(ChangeEventTypeF.DELETE, el));
                return el;
            } catch (IllegalArgumentException | ValidationException e) {
                System.err.println(e);
            }
        }
        throw  new IllegalArgumentException("the entity request for deleting does not exist!");
    }
    public boolean addServiceFriendship(Friendship friendship) {
        Predicate<Friendship> friendshipPredicate= el-> el.getId().equals(friendship.getId());
        List<Friendship> collect = StreamSupport.stream(repoFriendship.findAll().spliterator(), false).filter(friendshipPredicate).collect(Collectors.toList());
        collect.forEach(el -> {
            throw new IllegalArgumentException("exista deja");
        });
        try {
            repoFriendship.save(friendship);
            notifyObservers(new FriendTaskChangeEvent(ChangeEventTypeF.ADD, friendship));
            long c=friendship.getId().getLeft();
           User optionalUser=repo.findOne(friendship.getId().getLeft());
            User user1=null;
            User user2=null;
            if(optionalUser!=null) {
                user1 = repo.findOne(friendship.getId().getLeft());
            }
            if(optionalUser==null){
                throw new IllegalArgumentException("e null");
            }
           User optionalUser2=repo.findOne(friendship.getId().getRight());
            if(optionalUser2!=null) {
                user2 = repo.findOne(friendship.getId().getRight());
            }
            if(optionalUser2==null){
                throw new IllegalArgumentException("e null");
            }
            return true;
        }
        catch(IllegalArgumentException|ValidationException v){
            System.err.println(v);
            return false;
        }
    }
    public Iterable<Friendship> getAllFriendship() {
        return repoFriendship.findAll();
    }
    public User findName(String name){
        User u=repo.findOneName(name);
        return u;
    }

    public Iterable<Friendship> getAllFriendship(Long id) {
        return repoFriendship.findAll(id);
    }

    public Set<Friendship> getAllFriendship(int i, Long id, int number) {
        this.page=i;
        setSize(number);
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<Friendship> messagePage=repoFriendship.findAll(pageable,id);
        return messagePage.getContent().collect(Collectors.toSet());
    }
    public Set<Friendship> getNextFriendship( Long id) {
        this.page=page+1;
        return getAllFriendship(this.page,id,this.size);
    }
    public Set<Friendship> getPreviousFriendship( Long id) {
        this.page=page-1;
        return getAllFriendship(this.page,id,this.size);
    }

}
