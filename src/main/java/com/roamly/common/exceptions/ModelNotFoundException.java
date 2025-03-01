package com.roamly.common.exceptions;

public class ModelNotFoundException extends RuntimeException {

    public ModelNotFoundException(String entity, Object id) {
        super(entity + " with ID " + id + " was not found.");
    }
}