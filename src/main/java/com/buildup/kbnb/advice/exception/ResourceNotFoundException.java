package com.buildup.kbnb.advice.exception;

public class ResourceNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Can't find resource";
    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException() {
        super(MESSAGE);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
