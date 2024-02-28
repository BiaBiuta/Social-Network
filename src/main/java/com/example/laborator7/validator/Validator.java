package com.example.laborator7.validator;

public interface Validator<T> {
    void validate(T entityToValidate) throws ValidationException;
}
