package com.example.laborator7.validator;

import com.example.laborator7.domain.Message;
import com.example.laborator7.domain.User;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entityToValidate) throws ValidationException {
        if(entityToValidate.getFrom()==null){
            throw new ValidationException("the user must not be null");
        }
        if(entityToValidate.getAll()==null){
            throw new ValidationException("the user must not be null");
        }
        if(entityToValidate.getMessage().equals("")){
            throw new ValidationException("the message must not be empty");
        }
        if(entityToValidate.getDate()==null){
            throw new ValidationException("the date must not be null");
        }
    }
}
