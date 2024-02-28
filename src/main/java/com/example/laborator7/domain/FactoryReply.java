package com.example.laborator7.domain;



public class FactoryReply {
    private static FactoryReply instance=new FactoryReply();
    public static  FactoryReply getInstance(){
        return instance;
    }
    public ReplyMessage createInstance(Reply strategy ){
        switch(strategy){
            case ACCEPTED :

                return null;

            case REJECTED:

                return  null;
            case WAITING:
                return null;
        }
        return null;
    }
}
