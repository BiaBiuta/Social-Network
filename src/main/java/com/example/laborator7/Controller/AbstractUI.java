package org.example.Controller;
import org.example.service.Service;


public abstract class AbstractUI implements UI {
    protected Service service;
    public AbstractUI(Service<Long> service){
        this.service=service;
    }


}
