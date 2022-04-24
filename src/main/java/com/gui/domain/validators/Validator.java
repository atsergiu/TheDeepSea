package com.gui.domain.validators;

/*
 *
 * */
public interface Validator<T> {
    /**
     * Constructor for FriendshipFileRepository
     */
    void validate(T entity) throws ValidationException;
}