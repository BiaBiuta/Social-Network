package com.example.laborator7.utils.observer;

import com.example.laborator7.utils.events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}
