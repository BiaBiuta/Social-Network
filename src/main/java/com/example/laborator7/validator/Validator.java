package org.example.validator;

public interface Validator<T> {
    void validate(T entityToValidate) throws ValidationException;
}
