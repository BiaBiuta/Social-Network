package com.example.laborator7.validator;


import com.example.laborator7.domain.Friendship;
import com.example.laborator7.domain.User;

public class FactoryValidator {
    private static FactoryValidator instance=new FactoryValidator();
    public static  FactoryValidator getInstance(){
        return instance;
    }
    public Validator createInstance(StrategyValidator strategy ){
        switch(strategy){
            case USER :
                Validator<User> validator=new UserValidator();
                return validator;
            // mergeSort.execute();
            case FRIEND:
                Validator<Friendship> friendshipValidator=new FriendshipValidator();
                return  friendshipValidator;
        }
        return null;
    }
}
