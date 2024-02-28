package com.example.laborator7.validator;


import com.example.laborator7.domain.Friendship;

import java.util.Objects;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entityToValidate) throws ValidationException {
        if(Objects.equals(entityToValidate.getId().getRight(), entityToValidate.getId().getLeft())){
            throw new ValidationException("you can't formed a friendship with you");
        }
        if(entityToValidate.getId().getRight()==null || entityToValidate.getId().getLeft()==null){
            throw  new ValidationException("you can't formed a friendship with a friend which not exist");
        }
    }
}
