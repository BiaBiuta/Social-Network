package com.example.laborator7.service.serviceForPerson;

import com.example.laborator7.domain.CreatedTuple;
import com.example.laborator7.domain.Entity;
import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PageableImplementation;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.utils.events.user.ChangeEventType;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.ValidationException;
import javafx.scene.robot.Robot;
import java.awt.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServiceDbForUser implements Observable<UserTaskChangeEvent> {
    private PagingRepository<Long, User> repo;

    private FriendDataBaseRepository repoFriendship;
    private List<Observer<UserTaskChangeEvent>> observers=new ArrayList<>();
    private int page = 0;
    private int size = 10;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }
    public void setPageable(Pageable pageable) {
        this.pageable = pageable;
    }
    public ServiceDbForUser(PagingRepository<Long, User> repo, FriendDataBaseRepository repoFriendship ){
        this.repo = repo;
        this.repoFriendship = repoFriendship;
    }
    private <T> Iterable <T> filter(Iterable <T> list, Predicate<T> cond)
    {
        List<T> rez=new ArrayList<>();
        list.forEach((T x)->{if (cond.test(x)) rez.add(x);});
        return rez;
    }
    public User findUser(Long id){
        return repo.findOne(id);
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
    public User findOneServiceUser(String id) {
        return repo.findOneName(id);
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
    public Iterable<User> getAllUserFromPage(int page,Long id) {
        return getAllUserOnPage(page,size,id);
    }


    public Set<User> getAllUserOnPage(int page,int numberPerPg,Long id) {
        this.page=page;
        setPageSize(numberPerPg);
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<User> studentPage = repo.findAll(pageable, id);
        return studentPage.getContent().collect(Collectors.toSet());
    }
    public Set<User>getPreviousUser(Long id) {
        this.page--;
        return getAllUserOnPage(this.page,size,id);
    }
    public Set<User> getNextUser(Long id) {
        this.page++;
        return getAllUserOnPage(this.page,size,id);
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
    public Page<User> getAllUser(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public User findUserPassword(String password, String username) {
        return repo.findUserPassword(password,username);
    }
}
