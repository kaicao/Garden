package com.kaicao.garden.utils;

/**
 * Created by kaicao on 26/10/14.
 */
public class RepositoryException extends ServiceException {
    private String message;
    private Throwable cause;

    public RepositoryException(String message, Throwable cause) {
        this.message = message;
        this.cause = cause;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
