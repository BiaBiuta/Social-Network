package com.example.laborator7.validator;


import com.example.laborator7.domain.User;

import java.util.Objects;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entityToValidate) throws ValidationException {
        if(Objects.equals(entityToValidate.getFirstName(), "")){
            throw new ValidationException("first name must not be empty");
        }
        if(Objects.equals(entityToValidate.getLastName(), "")){
            throw new ValidationException("first name must not be empty");
        }
        if(entityToValidate.getFirstName().matches("^[a-z].*")){
            throw  new ValidationException("the name must first with capslock");
        }
        if(entityToValidate.getLastName().matches("^[a-z].*")){
            throw  new ValidationException("the name must first with capslock");
        }
    }
}
