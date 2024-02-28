package com.example.laborator7.utils.observer;

import com.example.laborator7.utils.events.Event;
import com.example.laborator7.utils.observer.Observer;

public interface Observable<E extends Event> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
