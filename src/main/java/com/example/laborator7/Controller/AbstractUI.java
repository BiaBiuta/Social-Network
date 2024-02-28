package com.example.laborator7.Controller;
import com.example.laborator7.service.Service;


public abstract class AbstractUI implements UI {
    protected Service service;
    public AbstractUI(Service<Long> service){
        this.service=service;
    }


}
