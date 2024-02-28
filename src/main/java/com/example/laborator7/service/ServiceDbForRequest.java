package com.example.laborator7.service;

import com.example.laborator7.domain.*;
import com.example.laborator7.repository.FriendDataBaseRepository;
import com.example.laborator7.repository.RequestPagingRepository;
import com.example.laborator7.repository.RequestRepository;
import com.example.laborator7.repository.UserDataBaseRepository;
import com.example.laborator7.repository.paging.Page;
import com.example.laborator7.repository.paging.Pageable;
import com.example.laborator7.repository.paging.PageableImplementation;
import com.example.laborator7.repository.paging.PagingRepository;
import com.example.laborator7.utils.events.friendship.FriendTaskChangeEvent;
import com.example.laborator7.utils.events.request.RequestChangeEvent;
import com.example.laborator7.utils.events.request.StatusChange;
import com.example.laborator7.utils.events.user.ChangeEventType;
import com.example.laborator7.utils.events.user.UserTaskChangeEvent;
import com.example.laborator7.utils.observer.Observable;
import com.example.laborator7.utils.observer.Observer;
import com.example.laborator7.validator.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ServiceDbForRequest implements Observable<RequestChangeEvent> {
    private UserDataBaseRepository repo;
    private RequestPagingRepository requestFriendshipPagingRepository;
    private FriendDataBaseRepository repoFriendship;
    private List<Observer<RequestChangeEvent>> observers=new ArrayList<>();
    private User u;
    private int page = 0;
    private int size=5 ;


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

    public ServiceDbForRequest(UserDataBaseRepository repo, RequestPagingRepository requestFriendshipPagingRepository, FriendDataBaseRepository repoFriendship) {
        this.repo = repo;
        this.requestFriendshipPagingRepository = requestFriendshipPagingRepository;
        this.repoFriendship=repoFriendship;
    }

    @Override
    public void addObserver(Observer<RequestChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<RequestChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(RequestChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
    public Entity<Long> deleteService(Long id) {
        try {
            RequestFriendship r=requestFriendshipPagingRepository.findOne(id);
            r.setStatus(String.valueOf(StatusChange.REJECTED));
            var x= requestFriendshipPagingRepository.update(r);
            notifyObservers(new RequestChangeEvent(StatusChange.REJECTED, r));
            return x;
        }
        catch(ValidationException | IllegalArgumentException e){
            System.err.println(e);
            return null;
        }

    }
    public RequestFriendship addServiceUser(RequestFriendship u) {
        RequestFriendship user=requestFriendshipPagingRepository.save(u);
        if(user != null) {
            notifyObservers(new RequestChangeEvent(StatusChange.PENDING,user));
        }
        return user;
    }
    public RequestFriendship deleteServiceUser(RequestFriendship u){
        if(Objects.equals(u.getStatus(), "APPROVED")){
            Long id1=u.getId1();
            Long id2=u.getId2();
            LocalDateTime date=u.getDate();
            Friendship fr=new Friendship(id1,id2);
            fr.setDate(date);
            CreatedTuple<Long,Long>id=new CreatedTuple<>(id1,id2);
            fr.setId(id);
            Friendship user=repoFriendship.delete(fr.getId());
        }
        u.setStatus(String.valueOf(StatusChange.REJECTED));
        requestFriendshipPagingRepository.update(u);
        notifyObservers(new RequestChangeEvent(StatusChange.REJECTED,u));

        return u;
    }
    public RequestFriendship updateServiceUser(RequestFriendship u) {
        Long id1=u.getId1();
        Long id2=u.getId2();
        LocalDateTime date=u.getDate();
        Friendship fr=new Friendship(id1,id2);
        fr.setDate(date);
        CreatedTuple<Long,Long>id=new CreatedTuple<>(id1,id2);
        fr.setId(id);
        Friendship user=null;
        if(!Objects.equals(u.getStatus(), "APPROVED")) {
        user=repoFriendship.save(fr);

            u.setStatus(String.valueOf(StatusChange.APPROVED));
            requestFriendshipPagingRepository.update(u);
        }
        if(user != null) {
            notifyObservers(new RequestChangeEvent(StatusChange.APPROVED,u));
        }
        return u;
    }
    public Iterable<RequestFriendship> getAllRequest(Long id) {
        return requestFriendshipPagingRepository.findAll(id);
    }
    public Set<RequestFriendship> getAllRequestOnPage(int page, Long id,Integer numberPGg){
        this.page=page;
        setSize(numberPGg);
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<RequestFriendship> messagePage= requestFriendshipPagingRepository.findAll(pageable,id);
        return messagePage.getContent().collect(Collectors.toSet());
    }
    public Set<RequestFriendship> getPreviousMessage(Long id){
        this.page=page-1;
        return getAllRequestOnPage(this.page,id,size);
    }
    public Set<RequestFriendship> getNextMessage(Long id){
        this.page=page+1;
        return getAllRequestOnPage(this.page,id,size);
    }

}
