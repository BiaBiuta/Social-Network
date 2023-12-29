package org.example.validator;

import org.example.domain.Friendship;
import org.example.domain.User;

public class FactoryValidator {
    private static FactoryValidator instance=new FactoryValidator();
    public static  FactoryValidator getInstance(){
        return instance;
    }
    public Validator createInstance(StrategyValidator strategy ){
        switch(strategy){
            case USER :
                Validator<User> validator=new validators.UserValidator();
                return validator;
            // mergeSort.execute();
            case FRIEND:
                Validator<Friendship> friendshipValidator=new FriendshipValidator();
                return  friendshipValidator;
        }
        return null;
    }
}
