package com.example.laborator7.service;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PageableImplementation;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.utils.events.ChangeEventType;
import com.example.laborator7.utils.events.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServiceDbForUser implements Observable<UserTaskChangeEvent> {
    private PagingRepository<Long, User> repo;
    private PagingRepository<CreatedTuple<Long,Long>, Friendship> repoFriendship;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();
    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

    public ServiceDbForUser(PagingRepository<Long, User> repo, PagingRepository<CreatedTuple<Long, Long>, Friendship> repoFriendship ){
        this.repo = repo;
        this.repoFriendship = repoFriendship;
    }
    private <T> Iterable <T> filter(Iterable <T> list, Predicate<T> cond)
    {
        List<T> rez=new ArrayList<>();
        list.forEach((T x)->{if (cond.test(x)) rez.add(x);});
        return rez;
    }

    @Override
    public void addObserver(Observer<UserTaskChangeEvent> e) {
            observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserTaskChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserTaskChangeEvent t) {
        observers.forEach(x->x.update(t));
    }

    public User addServiceUser(User u) {
        User user=repo.save(u);
        if(user != null) {
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.ADD,user));
        }
        return user;
    }

    public User updateServiceUser(User newTask) {
        User oldTask=repo.findOne(newTask.getId());
        if(oldTask!=null) {
            User res=repo.update(newTask);
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.UPDATE, newTask, oldTask));
            return res;
        }
        return oldTask;
    }

    public Iterable<User> getAllUser() {
        return repo.findAll();
    }
    public Set<User> getNextMessages() {
//        Pageable pageable = new PageableImplementation(this.page, this.size);
//        Page<MessageTask> studentPage = repo.findAll(pageable);
//        this.page++;
//        return studentPage.getContent().collect(Collectors.toSet());
        this.page++;
        return getMessagesOnPage(this.page);
    }

    public Set<User> getMessagesOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = repo.findAll(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }

    public Entity<Long> deleteServiceUser(Long id) {
        try {
            User userDeleted=repo.findOne(id);
            var x= repo.delete(id);
            notifyObservers(new UserTaskChangeEvent(ChangeEventType.DELETE, userDeleted));
            return x;
        }
        catch(ValidationException|IllegalArgumentException e){
            System.err.println(e);
            return null;
        }
    }
}
